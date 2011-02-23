/*
 * Copyright 2011 Wyona
 */
package org.wyona.yanel.impl.resources.navigation;

import org.wyona.yanel.core.navigation.Node;
import org.wyona.yanel.core.navigation.Sitetree;
import org.wyona.yanel.impl.resources.BasicXMLResource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.transform.Transformer;

/**
 * Data repository site tree generator.
 */
public class DataRepoSitetreeResource extends BasicXMLResource {
    private static Logger log = Logger.getLogger(DataRepoSitetreeResource.class);

    /**
     * Get size.
     * @see org.wyona.yanel.core.Resource
     */
    @Override
    public long getSize() throws Exception {
        return -1;
    }

    /**
     * Does resource exist?
     * @see org.wyona.yanel.core.Resource
     */
    @Override
    public boolean exists() throws Exception {
        return true;
    }

    /**
     * Get content as XML.
     * @param viewId Ignored by this function.
     * @see org.wyona.yanel.impl.BasicXMLResource
     */
    @Override
    public InputStream getContentXML(String viewId) throws Exception {
        Document doc = null;

        try {
            doc = org.wyona.commons.xml.XMLHelper.createDocument(null, "sitetree");
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }

        Element rootElement = doc.getDocumentElement();
        getSitetreeAsXML(doc, rootElement);

        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        org.wyona.commons.xml.XMLHelper.writeDocument(doc, baout);
        return new ByteArrayInputStream(baout.toByteArray());
    }

    /**
     * Get sitetree as XML.
     * @param doc The result document.
     * @param root The root element of the result document.
     */
    private void getSitetreeAsXML(Document doc, Element root) throws Exception {
        // Get name4path parameter
        String name4pathParameter = "path";
        if(getResourceConfigProperty("name4path-parameter") != null) {
            name4pathParameter = getResourceConfigProperty("name4path-parameter");
        }

        // Get node as XML.
        if(getEnvironment().getRequest().getParameter(name4pathParameter) != null) {
            getNodeAsXML(request.getParameter(name4pathParameter), doc, root);
        } else {
            getNodeAsXML("/", doc, root);
        }

    }

    /**
     * Get either children of node as XML (show-all-subnodes=false) or whole sitetree (show-all-subnodes=true).
     *
     * @param path Path of node
     * @param doc The result document.
     * @param root The root element of the result document.
     */
    private void getNodeAsXML(String path, Document doc, Element root) {
        // Should we show all subnodes?
        boolean showAllSubnodes = true;

        try {
            if(getResourceConfigProperty("show-all-subnodes") != null) {
                showAllSubnodes = Boolean.valueOf(getResourceConfigProperty("show-all-subnodes")).booleanValue();
            }
        } catch (Exception e) {
            log.info("Could not get property show-all-subnodes. Falling back to default value (true).");
        }

        String showAllSubnodesParameter = getParameterAsString("show-all-subnodes");
        if(showAllSubnodesParameter != null && Boolean.valueOf(showAllSubnodesParameter).booleanValue()) {
            showAllSubnodes = true;
        }

        // Get nodes from repository
        Sitetree sitetree = getRealm().getRepoNavigation();
        Node node = sitetree.getNode(getRealm(), path);

        // Construct XML.
        if(node != null) {
            if(node.isCollection()) {
                Element collectionElement = null;

                if(showAllSubnodes) {
                    collectionElement = (Element) root.appendChild(doc.createElement("collection"));
                    collectionElement.setAttribute("path", node.getPath());
                    collectionElement.setAttribute("name", node.getName());
                    Element labelElement = (Element) collectionElement.appendChild(doc.createElement("label"));
                    labelElement.appendChild(doc.createTextNode(node.getName()));
                }

                // Get all children
                Node[] children = node.getChildren();
                for(Node child : children) {
                    if(child.isCollection()) {
                        if(collectionElement == null) {
                            Element childCollectionElement = (Element) root.appendChild(doc.createElement("collection"));
                            childCollectionElement.setAttribute("path", node.getPath());
                            childCollectionElement.setAttribute("name", node.getName());
                            Element labelElement = (Element) childCollectionElement.appendChild(doc.createElement("label"));
                            labelElement.appendChild(doc.createTextNode(node.getName()));
                            //labelElement.appendChild(doc.createTextNode(node.getLabel()));
                        } else {
                            getNodeAsXML(child.getPath(), doc, collectionElement);
                        }
                    } else if(child.isResource()) {
                        Element resourceElement;
                        if (collectionElement == null) {
                            resourceElement = (Element) root.appendChild(doc.createElement("resource"));
                        } else {
                            resourceElement = (Element) collectionElement.appendChild(doc.createElement("resource"));
                        }
                        resourceElement.setAttribute("path", child.getPath());
                        resourceElement.setAttribute("name", child.getName());
                        Element labelElement = (Element) resourceElement.appendChild(doc.createElement("label"));
                        labelElement.appendChild(doc.createTextNode(child.getName()));
                        //labelElement.appendChild(doc.createTextNode(child.getLabel()));
                    } else {
                        Element nothingElement = (Element) root.appendChild(doc.createElement("neither-resource-nor-collection"));
                        nothingElement.setAttribute("path", child.getPath());
                        nothingElement.setAttribute("name", child.getName());
                    }
                }
            } else {
                Element resourceElement = (Element) root.appendChild(doc.createElement("resource"));
                resourceElement.setAttribute("path", node.getPath());
                resourceElement.setAttribute("name", node.getName());
                Element labelElement = (Element) resourceElement.appendChild(doc.createElement("label"));
                labelElement.appendChild(doc.createTextNode(node.getName()));
            }
        } else {
            String errorMessage = "Node is null! (For path: " + path + ")";
            Element exceptionElement = (Element) root.appendChild(doc.createElement("resource"));
            exceptionElement.appendChild(doc.createTextNode(errorMessage));
            log.error(errorMessage);
        }
    }
    
    /**
     * Pass transformer parameters.
     */
    @Override
    protected void passTransformerParameters(Transformer transformer) throws Exception {
        super.passTransformerParameters(transformer);

        try {
            String resourceConfigPropertyDomain = getResourceConfigProperty("domain");
            if(resourceConfigPropertyDomain != null) {
                transformer.setParameter("domain", resourceConfigPropertyDomain);
            }
        } catch(Exception e) {
            log.error("Could not get property domain. Domain will not be available within transformer chain.");
            log.error(e.getMessage(), e);
        }
    }
}
