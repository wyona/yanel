/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.ResourceTypeDefinition;
import org.wyona.yanel.core.ResourceTypeRegistry;
import org.wyona.yanel.core.api.attributes.CreatableV2;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.navigation.Node;
import org.wyona.yanel.core.navigation.Sitetree;
import org.wyona.yanel.core.serialization.SerializerFactory;
import org.wyona.yanel.core.source.ResourceResolver;
import org.wyona.yanel.core.transformation.I18nTransformer2;
import org.wyona.yanel.core.transformation.XIncludeTransformer;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.core.util.ResourceAttributeHelper;

import org.apache.log4j.Category;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.serializer.Serializer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationUtil;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 */
public class ResourceCreatorResource extends Resource implements ViewableV2{
    private static Category log = Category.getInstance(ResourceCreatorResource.class);
    private boolean ajaxBrowser = false;

    /**
     *
     */
    public ResourceCreatorResource() {
    }

    /**
     *
     */
    public boolean exists() {
        return true;
    }

    /**
     *
     */
    public long getSize() {
        return -1;
    }

    /**
     *
     */
    public String getMimeType(String viewId) {
        if (viewId != null && viewId.equals("source")) return "application/xml";
        return "application/xhtml+xml";
    }

    /**
     *
     */
    public View getView(String viewId) {
        if(request.getHeader("User-Agent").indexOf("rv:1.7") < 0) {
            ajaxBrowser = true;
        }
        
        View view = new View();
        String mimeType = getMimeType(viewId);
        view.setMimeType(mimeType);

        try {
            org.wyona.yarep.core.Repository repo = getRealm().getRepository();

            if (viewId != null && viewId.equals("source")) {
                view.setInputStream(new java.io.StringBufferInputStream(getScreen()));
                view.setMimeType("application/xml");
                return view;
            }

            String[] xsltPath = getXSLTPath(getPath());
            if (xsltPath != null) {
                
                // create reader:
                XMLReader xmlReader = XMLReaderFactory.createXMLReader();
                CatalogResolver catalogResolver = new CatalogResolver();
                xmlReader.setEntityResolver(catalogResolver);
                
                // create xslt transformer:
                SAXTransformerFactory tf = (SAXTransformerFactory)TransformerFactory.newInstance();
                
                TransformerHandler[] xsltHandlers = new TransformerHandler[xsltPath.length];
                for (int i = 0; i < xsltPath.length; i++) {
                    xsltHandlers[i] = tf.newTransformerHandler(new StreamSource(repo.getNode(xsltPath[i]).getInputStream()));
                    xsltHandlers[i].getTransformer().setParameter("yanel.path.name", PathUtil.getName(getPath()));
                    xsltHandlers[i].getTransformer().setParameter("yanel.path", getPath());
                    xsltHandlers[i].getTransformer().setParameter("yanel.back2context", PathUtil.backToContext(realm, getPath()));
                    xsltHandlers[i].getTransformer().setParameter("yarep.back2realm", PathUtil.backToRealm(getPath()));
                   
                    xsltHandlers[i].getTransformer().setParameter("language", getRequestedLanguage());
                }
                
                // create i18n transformer:
                I18nTransformer2 i18nTransformer = new I18nTransformer2("global", getRequestedLanguage(), getRealm().getDefaultLanguage());
                i18nTransformer.setEntityResolver(catalogResolver);
                
                // create xinclude transformer:
                XIncludeTransformer xIncludeTransformer = new XIncludeTransformer();
                ResourceResolver resolver = new ResourceResolver(this);
                xIncludeTransformer.setResolver(resolver);
                
                // create serializer:
                Serializer serializer = SerializerFactory.getSerializer(SerializerFactory.XHTML_STRICT);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                
                // chain everything together (create a pipeline):
                xmlReader.setContentHandler(xsltHandlers[0]);
                for (int i=0; i<xsltHandlers.length-1; i++) {
                    xsltHandlers[i].setResult(new SAXResult(xsltHandlers[i+1]));
                }
                xsltHandlers[xsltHandlers.length-1].setResult(new SAXResult(xIncludeTransformer));
                xIncludeTransformer.setResult(new SAXResult(i18nTransformer));
                i18nTransformer.setResult(new SAXResult(serializer.asContentHandler()));
                serializer.setOutputStream(baos);
                
                // execute pipeline:
                xmlReader.parse(new InputSource(new java.io.StringBufferInputStream(getScreen())));
                
                // write result into view:
                view.setInputStream(new ByteArrayInputStream(baos.toByteArray()));
                return view;
            } else {
                log.debug("Mime-Type: " + mimeType);
                view.setInputStream(new java.io.StringBufferInputStream(getScreen()));
                return view;
            }
        } catch(Exception e) {
            log.error(e + " (" + getPath() + ", " + getRealm() + ")", e);
        }
        
        view.setInputStream(new java.io.StringBufferInputStream(getScreen()));
        return view;
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        ViewDescriptor[] vd = new ViewDescriptor[2];
        vd[0] = new ViewDescriptor("default");
        vd[0].setMimeType(getMimeType(null));
        vd[1] = new ViewDescriptor("source");
        vd[1].setMimeType(getMimeType("source"));
        return vd;
    }

    /**
     * Flow
     */
    private String getScreen() {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        sb.append("<head><title>create resource</title>");
        sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + PathUtil.getResourcesHtdocsPath(this) + "css/resource-creator.css\"/>");
        sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + PathUtil.getGlobalHtdocsPath(this) + "yanel-css/progressBar.css\"/>");
        sb.append("<script src=\"" + PathUtil.getGlobalHtdocsPath(this) + "yanel-js/prototype.js\" type=\"text/javascript\"></script>");
        sb.append("<script src=\"" + PathUtil.getGlobalHtdocsPath(this) + "yanel-js/progressBar.js\" type=\"text/javascript\"></script>");
        sb.append("<script src=\"" + PathUtil.getGlobalHtdocsPath(this) + "yanel-js/sorttable.js\" type=\"text/javascript\"></script>");
        sb.append("<script src=\"" + PathUtil.getResourcesHtdocsPath(this)+ "js/ajaxlookup.js\" type=\"text/javascript\"></script>");
        sb.append("</head>");
        sb.append("<body>");

        HttpServletRequest request = getRequest();
        Enumeration parameters = request.getParameterNames();
        if (!parameters.hasMoreElements()) {
            getSelectResourceTypeScreen(sb);
        } else {
            if (request.getParameter("save-as") != null) {
                // NOTE: Save as has been merged with getResourceScreen because otherwise uploading of data would be rather cumbersome.
                //getSaveAsScreen(sb);
                getNoSuchScreen(sb);
            } else if (request.getParameter("save") != null) {
                getSaveScreen(sb);
            } else if (request.getParameter("lookup") != null) {
                return getLookUp().toString();
            } else if (request.getParameter("resource-type") != null) {
                getResourceScreen(sb);
            } else {
                log.info("Fallback ...");
                //getNoSuchScreen(sb);
                getSelectResourceTypeScreen(sb);
            }
        }

        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    /**
     *
     */
    private void getSelectResourceTypeScreen(StringBuffer sb) {
        sb.append("<h4>Create new page (step 1)</h4>");
        sb.append("<h2>Select template (resp. resource type)</h2>");
        sb.append("<form>");

        ResourceTypeRegistry rtr = new ResourceTypeRegistry();

        ResourceTypeDefinition[] rtds = getResourceTypeDefinitions();
        if (rtds != null) {
        sb.append("Template (resp. resource type): <select name=\"resource-type\">");
        for (int i = 0; i < rtds.length; i++) {
            try {
                Resource resource = rtr.newResource(rtds[i].getResourceTypeUniversalName());
                if (resource != null && ResourceAttributeHelper.hasAttributeImplemented(resource, "Creatable", "2")) {
                    sb.append("<option value=\"" + rtds[i].getResourceTypeNamespace() + "::" + rtds[i].getResourceTypeLocalName() + "\">" + getDisplayName(rtds[i].getResourceTypeLocalName(), rtds[i].getResourceTypeNamespace()) + "</option>");
                } else {
                    log.warn("Resource type: " + rtds[i] + " does not implement CreatableV2 interface!");
                }
            } catch(Exception e) {
                log.error(e);
            }
        }

        sb.append("</select>");
        } else {
        sb.append("<p>No resource types!</p>");
        }
        sb.append("<br/><input type=\"submit\" value=\"Next\"/>");
        sb.append("</form>");
    }

    /**
     *
     */
    private void getNoSuchScreen(StringBuffer sb) {
        sb.append("<p>No such screen!</p>");
    }

    /**
     * Save screen
     */
    private void getSaveScreen(StringBuffer sb) {
        sb.append("<h4>Create new page (step 3)</h4>");

        Path pathOfNewResource = null;
        try {
            pathOfNewResource = create();
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            sb.append("<p>Exception: "+e.getMessage()+"</p>");
            return;
        }

        sb.append("<h2>Resource has been created</h2>");

        HttpServletRequest request = getRequest();
        Enumeration parameters = request.getParameterNames();
        if (parameters.hasMoreElements()) {
            sb.append("<ul>");
            while (parameters.hasMoreElements()) {
                String parameter = (String) parameters.nextElement();
                if (parameter.indexOf("rp.") == 0) {
                    sb.append("<li>"+parameter+": "+request.getParameter(parameter)+"</li>");
                }
            }
            sb.append("</ul>");
        }

        if (log.isDebugEnabled()) log.debug("New Resource: " + PathUtil.backToRealm(getPath()) + ", " + pathOfNewResource);
        // NOTE: Back to realm has the form of ./ or ../ or ../../ etc., hence drop the leading slash!
        String href = PathUtil.backToRealm(getPath()) + pathOfNewResource.toString().substring(1);
        sb.append("<p>New resource can be accessed at: <a href=\"" + href + "\">" + href + "</a></p>");
    }

    /**
     *
     */
    private void getResourceScreen(StringBuffer sb) {
        String rtps = getRequest().getParameter("resource-type");
        String resNamespace = rtps.substring(0, rtps.indexOf("::"));
        String resName = rtps.substring(rtps.indexOf("::") + 2);
        ResourceTypeRegistry rtr = new ResourceTypeRegistry();

        if (getRequest().getParameter("create-new-folder") != null && !getRequest().getParameter("create-new-folder").equals("")) {
            try {
                create(getRequest().getParameter("create-new-folder"), getRequest().getParameter("lookin"), "http://www.wyona.org/yanel/resource/1.0::directory");
            } catch (Exception e) {
                sb.append("<p>Could not create folder. Exception: " + e + "</p>");
                log.error(e.getMessage(), e);
            }
        }
        
        
        try {
            String universalName = "<{"+ resNamespace +"}"+ resName +"/>";
            log.debug("Universal Name: " + universalName);
            Resource resource = rtr.newResource(universalName);
            if (resource != null) {
                if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Creatable", "2")) {

                    sb.append("<h4>Create new page (step 2)</h4>");
                    sb.append("<h2>Enter/Select template (resp. resource) specific parameters and \"Save As\"</h2>");
                    sb.append("<p>Template (resp. resource type): " + resName + " ("+resNamespace+")</p>");
                    sb.append("<form enctype=\"multipart/form-data\" method=\"post\">");
                    // TODO: Add this parameter to the continuation within the session!
                    sb.append("<input type=\"hidden\" name=\"resource-type\" value=\"" + rtps + "\"/>");

                    Property[] defaultProperties = getDefaultProperties(resName, resNamespace);
                    String[] propertyNames = ((CreatableV2) resource).getPropertyNames();
                    if ((propertyNames != null && propertyNames.length > 0) || defaultProperties != null) {
                        sb.append("<p>Resource specific properties:</p>");
                    } else {
                        sb.append("<p>No resource specific properties!</p>");
                    }

                    if (propertyNames != null && propertyNames.length > 0) {
                        sb.append("<table border=\"1\">");
                        for (int i = 0; i < propertyNames.length; i++) {
                            sb.append("<tr><td>" + propertyNames[i] + ":&#160;&#160;&#160;</td><td>");
                            String propertyType = ((CreatableV2) resource).getPropertyType(propertyNames[i]);
                            if (propertyType != null && propertyType.equals(CreatableV2.TYPE_UPLOAD)) {
                                sb.append("<input type=\"file\" name=\"rp." + propertyNames[i] + "\"/><br/>");
                    } else if (propertyType != null && propertyType.equals(CreatableV2.TYPE_SELECT)) {
                                Object defaultValues = ((CreatableV2) resource).getProperty(propertyNames[i]);
                                if (defaultValues instanceof java.util.HashMap) {
                                    sb.append("<select name=\"rp." + propertyNames[i] + "\">");
                                    sb.append("  <option value=\"*\">*</option>");
                                    sb.append("  <option value=\"public\">public</option>");
                                    sb.append("  <option value=\"private\">private</option>");
                                    sb.append("</select><br/>");
                                } else {
                                    sb.append("Exception: Parameter doesn't seem to be a of type SELECT: " + propertyNames[i]);
                                }
                            } else {
                                Object value = ((CreatableV2) resource).getProperty(propertyNames[i]);
                                if (value == null) {
                                    sb.append("<input name=\"rp." + propertyNames[i] + "\" value=\"\" size=\"60\"/><br/>");
                                } else {
                                    sb.append("<input name=\"rp." + propertyNames[i] + "\" value=\"" + value + "\" size=\"60\"/><br/>");
                                }
                            }
                            sb.append("</td></tr>");
                        }
                        sb.append("</table>");
                    }
                    if (defaultProperties != null) {
                        for (int i = 0; i < defaultProperties.length; i++) {
                            sb.append("<p>Default property: " + defaultProperties[i] + "</p>");
                            sb.append("<input type=\"hidden\" name=\"rp." + defaultProperties[i].getName() + "\" value=\"" + defaultProperties[i].getValue() + "\"/><br/>");
                        }
                    }

                    //sb.append("<br/><br/><input type=\"submit\" value=\"Save As\" name=\"save-as\"/>");

                    // TODO: Display repository navigation of this path ...
                    Sitetree sitetree = (Sitetree) getYanel().getBeanFactory().getBean("repo-navigation");
                    Node node = null;
                    String lookinPath = getRequest().getParameter("lookin");
                    if (lookinPath != null) {
                        node = sitetree.getNode(getRealm(), lookinPath);
                    } else {
                        node = sitetree.getNode(getRealm(), getPath());
                    }
                    if (node.isCollection()) {
                        if(log.isDebugEnabled()) log.debug("Is Collection: " + node.getName());
                    } else if (node.isResource()) {
                        if (log.isDebugEnabled()) log.debug("Is Resource: " + node.getName());
                        node = node.getParent();
                    } else {
                        log.error("Neither collection nor resource: " + getPath());
                    }

                    sb.append("<br/><br/>");
                    
                    sb.append("<div id=\"lookup\">");
                    sb.append(getLookUp());
                    sb.append("</div>");
                    
                    sb.append("</form>");

                    // TODO: Display realm navigation (sitetree, topic map, ...) resp. introduce another step
                }
            }
        } catch (Exception e) {
            sb.append("<p>Exception: " + e + "</p>");
            log.error(e.getMessage(), e);
        }
    }
    
    /**
     * Creates new resource
     * @return Path of new resource
     */
    private Path create() throws Exception {
        return create(getRequest().getParameter("create-name"), getRequest().getParameter("lookin"), getRequest().getParameter("resource-type"));
    }

    /**
     * Creates new resource
     * @param String createName
     * @param String lookinPath
     * @param String resourceType
     * @return Path of new resource
     */
    private Path create(String createName, String lookinPath, String resourceType) throws Exception {
        org.wyona.yanel.core.map.Realm realm = getRealm();
        Path pathOfResourceCreator = new Path(getPath());
        
        org.wyona.commons.io.Path parent = new org.wyona.commons.io.Path(pathOfResourceCreator.toString()).getParent();
        
        Path pathOfNewResource = null;
        
        if(parent.equals("null")) {
            // if pathOfResourceCreator is ROOT
            pathOfNewResource = new Path("/" + lookinPath + "/" + createName);
        } else if(parent.toString().equals("/")){
            pathOfNewResource = new Path(parent + "/" + lookinPath + "/" + createName);
        } else {
            if (log.isDebugEnabled()) log.debug("Parent: " + parent + ", Lookin-path: " + lookinPath + ", Create Name: " + createName);
            pathOfNewResource = new Path("/" + lookinPath + "/" + createName);
        }
        
        if (log.isDebugEnabled()) log.debug("Path of new resource: " + pathOfNewResource);
        pathOfNewResource = new Path(removeTooManySlashes(pathOfNewResource.toString()));
        if (log.isDebugEnabled()) log.debug("Path of new resource without too many slashes: " + pathOfNewResource);
        
        String rtps = resourceType;
        String resNamespace = rtps.substring(0, rtps.indexOf("::"));
        String resName = rtps.substring(rtps.indexOf("::") + 2);
        Resource newResource = yanel.getResourceManager().getResource(getEnvironment(), realm, pathOfNewResource.toString(), new ResourceConfiguration(resName, resNamespace, null));
        
        if (newResource != null) {
            if (ResourceAttributeHelper.hasAttributeImplemented(newResource, "Creatable", "2")) {
                ((CreatableV2) newResource).create(request);
                createResourceConfiguration(newResource);
            } else {
                throw new Exception("creation NOT successfull!");
            }
        } else {
            throw new Exception("creation NOT successful (newResource == null)!");
            
        }
        return pathOfNewResource;
    }

    /**
     * Create resource configuration (yanel-rc)
     */
    private void createResourceConfiguration(Resource newResource) throws Exception {
        StringBuffer rcContent = new StringBuffer("<?xml version=\"1.0\"?>\n\n");
        rcContent.append("<yanel:resource-config xmlns:yanel=\"http://www.wyona.org/yanel/rti/1.0\">\n");
        rcContent.append("<yanel:rti name=\"" + newResource.getRTD().getResourceTypeLocalName() + "\" namespace=\"" + newResource.getRTD().getResourceTypeNamespace() + "\"/>\n\n");
        java.util.HashMap rtiProperties = ((CreatableV2) newResource).createRTIProperties(request);
        if (rtiProperties != null) {
            if (log.isDebugEnabled()) log.debug(rtiProperties + " " + PathUtil.getRTIPath(newResource.getPath()));
            java.util.Iterator iterator = rtiProperties.keySet().iterator();
            while (iterator.hasNext()) {
                String property = (String) iterator.next();
                String value = (String) rtiProperties.get(property);
                if (value != null) {
                    rcContent.append("<yanel:property name=\"" + property + "\" value=\"" + value + "\"/>\n");
                    if(log.isDebugEnabled()) log.debug("Set Property: " + property + ", " + value);
                } else {
                    log.warn("Property value is null: " + property);
                }
            }
        } else {
            log.warn("No RTI properties: " + newResource.getPath());
        }
        rcContent.append("</yanel:resource-config>");


        org.wyona.yarep.core.Repository rcRepo = newResource.getRealm().getRTIRepository();
        org.wyona.commons.io.Path newRCPath = new org.wyona.commons.io.Path(PathUtil.getRCPath(newResource.getPath()));
        if (log.isDebugEnabled()) log.debug(newRCPath);
        org.wyona.yanel.core.util.YarepUtil.addNodes(rcRepo, newRCPath.toString(), org.wyona.yarep.core.NodeType.RESOURCE);

        java.io.Writer writer = new java.io.OutputStreamWriter(rcRepo.getNode(newRCPath.toString()).getOutputStream());
        writer.write(rcContent.toString());
        writer.close();
    }

    /**
     * Get resource type definitions
     */
    private ResourceTypeDefinition[] getResourceTypeDefinitions() {
        ResourceTypeRegistry rtr = new ResourceTypeRegistry();
        ResourceConfiguration rc = getConfiguration();
        Document customConfigDoc = rc.getCustomConfiguration();
        if (customConfigDoc != null) {
            Configuration config = ConfigurationUtil.toConfiguration(customConfigDoc.getDocumentElement());
            Configuration resourceTypesConfig = config.getChild("resource-types", false);
            if (resourceTypesConfig != null) {
                Configuration[] resourceTypeConfigs = resourceTypesConfig.getChildren("resource-type");
                if (resourceTypeConfigs.length == 0) return null;
                ResourceTypeDefinition[] rtds = new ResourceTypeDefinition[resourceTypeConfigs.length];
                for (int i = 0; i < resourceTypeConfigs.length; i++) {
                    try {
                        String universalName = "<{"+resourceTypeConfigs[i].getAttribute("namespace")+"}"+resourceTypeConfigs[i].getAttribute("name")+"/>";
                        rtds[i] = rtr.getResourceTypeDefinition(universalName);
                        log.debug("Resource Type: " + universalName);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        return null;
                    }
                }
                return rtds;
            }
        }
        ResourceTypeDefinition[] rtds = rtr.getResourceTypeDefinitions();
        return rtds;
    }

    /**
     * Get default properties from custom configuration
     */
    private Property[] getDefaultProperties(String resName, String resNamespace) {
        Document customConfigDoc = getConfiguration().getCustomConfiguration();
        if (customConfigDoc != null) {
            Configuration config = ConfigurationUtil.toConfiguration(customConfigDoc.getDocumentElement());
            Configuration resourceTypesConfig = config.getChild("resource-types", false);
            if (resourceTypesConfig != null) {
                Configuration[] resourceTypeConfigs = resourceTypesConfig.getChildren("resource-type");
                if (resourceTypeConfigs.length == 0) return null;
                for (int i = 0; i < resourceTypeConfigs.length; i++) {
                    try {
                        if (resourceTypeConfigs[i].getAttribute("namespace").equals(resNamespace) && resourceTypeConfigs[i].getAttribute("name").equals(resName)) {
                            log.debug("Resource Type Found: " + resName + ", " + resNamespace);
                            Configuration[] propertyConfigs = resourceTypeConfigs[i].getChildren("property");
                            Property[] props = new Property[propertyConfigs.length];
                            for (int k = 0; k < propertyConfigs.length; k++) {
                                props[k] = new Property(propertyConfigs[k].getAttribute("name"), propertyConfigs[k].getAttribute("value"));
                                log.debug("Property: " + props[k]);
                            }
                            return props;
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get the display name from custom configuration
     */
    private String getDisplayName(String resName, String resNamespace) {
        Document customConfigDoc = getConfiguration().getCustomConfiguration();
        if (customConfigDoc != null) {

            try {
                org.jdom.Document jdomDocument = new org.jdom.input.DOMBuilder().build(customConfigDoc);
                org.jdom.xpath.XPath xpath = org.jdom.xpath.XPath.newInstance("/yanel:custom-config/rc:resource-types/rc:resource-type[@name='" + resName + "']/rc:display-name");
                xpath.addNamespace("yanel", "http://www.wyona.org/yanel/rti/1.0");
                xpath.addNamespace("rc", "http://www.wyona.org/yanel/resource/resource-creator/1.0");
                org.jdom.Element displayNameElement = (org.jdom.Element) xpath.selectSingleNode(jdomDocument);
                if (displayNameElement != null) {
                    // TODO: It seems like document does not contain text nodes ...
                    if (log.isDebugEnabled()) log.debug("Display name: " + displayNameElement + " :: " + displayNameElement.getText() + " :: " + displayNameElement.getName());
                    return displayNameElement.getText();
                } else {
                    log.warn("No display name: " + resName);
                    return resName;
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return resName;
            }
        }
        return resName;
    }
    
    private StringBuffer getLookUp() {
        StringBuffer sb = new StringBuffer("");
        Sitetree sitetree = (Sitetree) getYanel().getBeanFactory().getBean("repo-navigation");
        Node node = null;
        String lookinPath = getRequest().getParameter("lookin");
        if (lookinPath != null) {
            node = sitetree.getNode(getRealm(), lookinPath);
        } else {
            node = sitetree.getNode(getRealm(), getPath());
        }
        if (node.isCollection()) {
            if(log.isDebugEnabled()) log.debug("Is Collection: " + node.getName());
        } else if (node.isResource()) {
            if (log.isDebugEnabled()) log.debug("Is Resource: " + node.getName());
            node = node.getParent();
        } else {
            log.error("Neither collection nor resource: " + getPath());
        }
        String rtps = getRequest().getParameter("resource-type");
        String resNamespace = rtps.substring(0, rtps.indexOf("::"));
        String resName = rtps.substring(rtps.indexOf("::") + 2);
        
        sb.append("<table id=\"resourceCreatorSaveAsTable\"><tr><td colspan=\"2\">Save as:</td></tr>");
        sb.append("<tr><td>Look in: " + node.getPath() + "&#160;&#160;&#160;</td><td>New folder: <input type=\"text\" name=\"create-new-folder\"/>&#160;<input type=\"image\" src=\"" + PathUtil.getGlobalHtdocsPath(this) + "yanel-img/icons/folder-new.png\" alt=\"make a new folder\"/> ");
        
        String parent = "/";
        if (!node.getPath().equals("/")) {
            parent = new org.wyona.commons.io.Path(node.getPath()).getParent().toString();
        }
        if (log.isDebugEnabled()) log.debug("Parent: " + parent);

        if (ajaxBrowser) {
            sb.append("<a href=\"JavaScript:ajaxlookup('" + resNamespace + "::" + resName + "', '" + parent + "')\"><img src=\"" + PathUtil.getGlobalHtdocsPath(this) + "yanel-img/icons/go-up.png\" alt=\"go up\" border=\"0\"/></a>");
        } else {
            sb.append("<a href=\"?lookin=" + parent + "&amp;resource-type=" + resNamespace + "::" + resName + "\"><img src=\"" + PathUtil.getGlobalHtdocsPath(this) + "yanel-img/icons/go-up.png\" alt=\"go up\" border=\"0\"/></a>");
        }
        sb.append("</td></tr>");

        sb.append("<tr><td colspan=\"2\">");

        sb.append("<div id=\"lookupfiles\">");
        sb.append("<table id=\"lookupfilesTable\" class=\"sortable\">");
        sb.append("<thead>");
        sb.append("<tr><th>Type</th><th>Name</th><th>Resource Type</th></tr>");
        sb.append("</thead>");
        sb.append("<tbody>");
                Node[] children = node.getChildren();
                for (int i = 0; i < children.length; i++) {
                    String resourceTypeName;
                    try {
                        resourceTypeName = yanel.getResourceManager().getResource(getEnvironment(), realm, children[i].getPath()).getResourceTypeLocalName();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        resourceTypeName = "?";
                    }
                    if (children[i].isCollection()) {
                        // TODO: Also append resource specific parameters (AJAX ...)
                        if (ajaxBrowser) {
                            sb.append("<tr><td sorttable_customkey=\"Collection\"><a href=\"JavaScript:ajaxlookup('" + resNamespace + "::" + resName + "', '" + node.getPath() + children[i].getName() + "/')\"><img class=\"lookupicon\" src=\"" + PathUtil.getGlobalHtdocsPath(this) + "yanel-img/icons/folder.png\" alt=\"Collection:\"/></a></td><td><a href=\"JavaScript:ajaxlookup('" + resNamespace + "::" + resName + "', '" + node.getPath() + children[i].getName() + "/')\">" + children[i].getName() + "</a></td><td>" + resourceTypeName  + "</td></tr>");
                        } else {
                            sb.append("<tr><td sorttable_customkey=\"Collection\"><a href=\"?lookin=" + node.getPath() + children[i].getName() + "/&amp;resource-type=" + resNamespace + "::" + resName + "\"><img class=\"lookupicon\" src=\"" + PathUtil.getGlobalHtdocsPath(this) + "yanel-img/icons/folder.png\" alt=\"Collection:\"/></a></td><td><a href=\"?lookin=" + node.getPath() + children[i].getName() + "/&amp;resource-type=" + resNamespace + "::" + resName + "\">" + children[i].getName() + "</a></td><td>" + resourceTypeName  + "</td></tr>");
                        }
                    } else if (children[i].isResource()) {
                sb.append("<tr><td sorttable_customkey=\"Resource\"><img class=\"lookupicon\" src=\"" + PathUtil.getGlobalHtdocsPath(this) + "yanel-img/icons/text-x-generic.png\" alt=\"Resource:\"/></td><td>"+children[i].getName()+"</td><td>" + resourceTypeName  + "</td></tr>");
                    } else {
                sb.append("<tr><td>?</td><td>"+children[i].getName()+"</td><td>-</td></tr>");
                    }
                }
        
        sb.append("</tbody>");
        sb.append("</table>");
        sb.append("</div>");
        sb.append("</td></tr>");

        sb.append("<tr><td colspan=\"2\">");
       
        String createName = getRequest().getParameter("create-name");
        if (createName != null) {
            sb.append("New name: <input type=\"text\" name=\"create-name\" value=\"" + createName + "\"/>");
        } else {
            sb.append("New name: <input type=\"text\" name=\"create-name\"/>");
        }
        sb.append("</td></tr>");

        sb.append("<tr><td colspan=\"2\" align=\"right\">");
        sb.append("<input type=\"hidden\" name=\"lookin\" value=\"" + node.getPath() + "\"/>");
        sb.append("<input type=\"submit\" value=\"Save new resource\" name=\"save\"/></td></tr>");
        sb.append("</table>");
        
        return sb;
    }
    
    /**
     * Get XSLT path
     */
    private String[] getXSLTPath(String path) throws Exception {
        String[] xsltPath = getResourceConfigProperties("xslt");
        if (xsltPath != null) return xsltPath;
        log.info("No XSLT Path within: " + path);
        return null;
    }

    /**
     * Remove slashes if there are too many, e.g. /foo//bar.html is being transformed into /foo/bar.html
     */
    private String removeTooManySlashes(String path) {
        StringBuffer sb = new StringBuffer();
        boolean previousCharWasSlash = false;
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c == '/' && previousCharWasSlash) {
                if (log.isDebugEnabled()) log.debug("Do not append this slash: " + i);
            } else {
                sb.append(c);
            }
            if (c == '/') {
                previousCharWasSlash = true;
            } else {
                previousCharWasSlash = false;
            }
        }
        return sb.toString();
    }
}

/**
 *
 */
class Property {

    private String name;
    private String value;

    /**
     *
     */
    public Property(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     *
     */
    public String getName() {
        return name;
    }

    /**
     *
     */
    public String getValue() {
        return value;
    }

    /**
     *
     */
    public String toString() {
        return name + " = " + value;
    }
}
