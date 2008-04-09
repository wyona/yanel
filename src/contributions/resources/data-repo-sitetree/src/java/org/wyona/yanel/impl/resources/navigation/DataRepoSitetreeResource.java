/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources.navigation;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.navigation.Node;
import org.wyona.yanel.core.navigation.Sitetree;
import org.wyona.yanel.impl.resources.BasicXMLResource;

import org.apache.log4j.Logger;

/**
 *
 */
public class DataRepoSitetreeResource extends BasicXMLResource {

    private static Logger log = Logger.getLogger(DataRepoSitetreeResource.class);

    /**
     *
     */
    public DataRepoSitetreeResource() {
    }

    /**
     *
     */
    public long getSize() throws Exception {
        return -1;
    }

    /**
     *
     */
    public boolean exists() throws Exception {
        return true;
    }

    /**
     *
     */
    public java.io.InputStream getContentXML(String viewId) throws Exception {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append(getSitetreeAsXML());
        //sb.append(getSitetreeAsXML(getPath().toString()));

        return new java.io.StringBufferInputStream(sb.toString());
    }

    /**
     * Get sitetree as XML
     */
    private String getSitetreeAsXML() {
        StringBuffer sb = new StringBuffer("<sitetree>");
        if (getRequest().getParameter("path") != null) {
            sb.append(getNodeAsXML(request.getParameter("path")));
        } else {
            sb.append(getNodeAsXML("/"));
        }

        // TODO: Sitetree generated out of RDF resources and statements
        /*
        com.hp.hpl.jena.rdf.model.Resource rootResource = getRealm().getSitetreeRootResource();
        sb.append(getNodeAsXML(rootResource));
        */
        sb.append("</sitetree>");

        return sb.toString();
    }

    /**
     * Get node as XML
     */
    private String getNodeAsXML(String path) {
    //private String getNodeAsXML(com.hp.hpl.jena.rdf.model.Resource resource) {
        //log.error("DEBUG: Path: " + path);
        Sitetree sitetree = getRealm().getRepoNavigation();
        Node node = sitetree.getNode(getRealm(), path);
        StringBuffer sb = new StringBuffer("");

        // TODO: Check for statements "parentOf" for this resource
        /*
        Statement[] st = resource.getStatements("parentOf");
        if (st.length > 0) {
            for (int i = 0; i < st.length; i++) {
                Resource child = st.getObject();
                URL url = getReal().getURLBuilder(child);
            }
        } else {
            // Is not a collection, there are no children
        }
        */

        if (node != null) {
            if (node.isCollection()) {
                sb.append("<collection path=\"" + path + "\" name=\"" + node.getName() + "\">");
                // TODO: ...
                sb.append("<label><![CDATA[" + node.getName() + "]]></label>");
                //sb.append("<label><![CDATA[" + node.getLabel() + "]]></label>");
                Node[] children = node.getChildren();
                for (int i = 0; i < children.length; i++) {
                    String childPath = path + "/" + children[i].getName();
                    if (path.equals("/")) {
                        childPath = path + children[i].getName();
                    }
                    //log.debug("Child path: " + childPath);

                    if (children[i].isCollection()) {
                        sb.append(getNodeAsXML(childPath));
                        //sb.append(getNodeAsXML(children[i].getPath()));
                    } else if (children[i].isResource()) {
                        sb.append("<resource path=\"" + childPath + "\" name=\"" + children[i].getName() + "\">");
                        //sb.append("<resource path=\"" + children[i].getPath() + "\" name=\"" + children[i].getName() + "\">");
                        // TODO ...
                        sb.append("<label><![CDATA[" + children[i].getName() + "]]></label>");
                        //sb.append("<label><![CDATA[" + children[i].getLabel() + "]]></label>");
                        sb.append("</resource>");
                    } else {
                        sb.append("<neither-resource-nor-collection path=\"" + childPath + "\" name=\"" + children[i].getName() + "\"/>");
                        //sb.append("<neither-resource-nor-collection path=\"" + children[i].getPath() + "\" name=\"" + children[i].getName() + "\"/>");
                    }
                }
                sb.append("</collection>");
            } else {
                sb.append("<resource path=\"" + path + "\" name=\"" + node.getName() + "\">");
                // TODO ...
                sb.append("<label><![CDATA[" + node.getName() + "]]></label>");
                //sb.append("<label><![CDATA[" + node.getLabel() + "]]></label>");
                sb.append("</resource>");
            }
        } else {
            String errorMessage = "node is null for path: " + path;
            sb.append("<exception>" + errorMessage + "</exception>");
            log.error(errorMessage);
        }
        return sb.toString();
    }
}
