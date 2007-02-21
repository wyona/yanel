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
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.core.util.ResourceAttributeHelper;

import org.apache.log4j.Category;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationUtil;

import org.w3c.dom.Document;

/**
 *
 */
public class ResourceCreatorResource extends Resource implements ViewableV2{
    private static Category log = Category.getInstance(ResourceCreatorResource.class);

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
        View view = new View();
        view.setMimeType(getMimeType(viewId));
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
	    } else if (request.getParameter("resource-type") != null) {
                getResourceScreen(sb);
            } else {
                getNoSuchScreen(sb);
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
        sb.append("<h4>Create resource (step 1)</h4>");
        sb.append("<h2>Select resource type</h2>");
        sb.append("<form>");

        ResourceTypeRegistry rtr = new ResourceTypeRegistry();

        ResourceTypeDefinition[] rtds = getResourceTypeDefinitions();
        if (rtds != null) {
        sb.append("Resource Type: <select name=\"resource-type\">");
        for (int i = 0; i < rtds.length; i++) {
            try {
                Resource resource = rtr.newResource(rtds[i].getResourceTypeUniversalName());
                if (resource != null && ResourceAttributeHelper.hasAttributeImplemented(resource, "Creatable", "2")) {
                    sb.append("<option value=\"" + rtds[i].getResourceTypeNamespace() + "::" + rtds[i].getResourceTypeLocalName() + "\">" + rtds[i].getResourceTypeLocalName() + "</option>");
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
     * OBSOLETE
     */
/*
    private void getSaveAsScreen(StringBuffer sb) {
        String rtps = getRequest().getParameter("resource-type");
        String resNamespace = rtps.substring(0, rtps.indexOf("::"));
        String resName = rtps.substring(rtps.indexOf("::") + 2);
        ResourceTypeRegistry rtr = new ResourceTypeRegistry();

        String universalName = "<{"+ resNamespace +"}"+ resName +"/>";
        log.debug("Universal Name: " + universalName);
        try {
            Resource resource = rtr.newResource(universalName);
            if (resource != null) {

        sb.append("<h4>Create resource (step 3)</h4>");
        sb.append("<h2>Save As</h2>");

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

        sb.append("<p>");
        sb.append("<form>");
        //sb.append("<form method=\"post\" enctype=\"multipart/form-data\">");
        // TODO: Add this parameter to the continuation within the session!
        sb.append("<input type=\"hidden\" name=\"resource-type\" value=\""+rtps+"\"/>");

        parameters = request.getParameterNames();
        while (parameters.hasMoreElements()) {
            String parameter = (String) parameters.nextElement();
            if (parameter.indexOf("rp.") == 0) {
                String propertyType = ((CreatableV2) resource).getPropertyType(parameter.substring(3));
                if (propertyType != null && propertyType.equals(CreatableV2.TYPE_UPLOAD)) {
                    sb.append("<input type=\"file\" name=\""+parameter+"\" value=\""+request.getParameter(parameter)+"\"/><br/>");
		} else if (propertyType != null && propertyType.equals(CreatableV2.TYPE_SELECT)) {
                    sb.append("<select name=\"parameter\">");
                    sb.append("</select>");
                } else {
                    sb.append("<input type=\"hidden\" name=\""+parameter+"\" value=\""+request.getParameter(parameter)+"\"/>");
                }
            }
        }

        String createName = request.getParameter("create-name");
        if (createName != null) {
            sb.append("Name: <input type=\"text\" name=\"create-name\" value=\"" + createName + "\"/>");
        } else {
            sb.append("Name: <input type=\"text\" name=\"create-name\"/>");
        }
        sb.append("<br/><input type=\"submit\" value=\"Save\" name=\"save\"/>");
        sb.append("</form>");
        sb.append("</p>");
        }
        } catch (Exception e) {
            sb.append("<p>Exception: "+e+"</p>");
            log.error(e);
        }
    }
*/

    /**
     * Save screen
     */
    private void getSaveScreen(StringBuffer sb) {
        sb.append("<h4>Create resource (step 3)</h4>");

        try {
            create();
        } catch(Exception e) {
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

        String createName = request.getParameter("create-name");
        sb.append("<p>New resource can be accessed at: <a href=\""+createName+"\">"+createName+"</a></p>");
    }

    /**
     *
     */
    private void getResourceScreen(StringBuffer sb) {
        String rtps = getRequest().getParameter("resource-type");
        String resNamespace = rtps.substring(0, rtps.indexOf("::"));
        String resName = rtps.substring(rtps.indexOf("::") + 2);
        ResourceTypeRegistry rtr = new ResourceTypeRegistry();

        try {
            String universalName = "<{"+ resNamespace +"}"+ resName +"/>";
            log.debug("Universal Name: " + universalName);
            Resource resource = rtr.newResource(universalName);
            if (resource != null) {
                if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Creatable", "2")) {

                    sb.append("<h4>Create resource (step 2)</h4>");
                    sb.append("<h2>Enter/Select resource specific parameters and \"Save As\"</h2>");
                    sb.append("<p>Resource Type: " + resName + " ("+resNamespace+")</p>");
                    sb.append("<form>");
                    // TODO: Add this parameter to the continuation within the session!
                    sb.append("<input type=\"hidden\" name=\"resource-type\" value=\"" + rtps + "\"/>");

                    Property[] defaultProperties = getDefaultProperties(resName, resNamespace);
                    String[] propertyNames = ((CreatableV2) resource).getPropertyNames();
                    if ((propertyNames != null && propertyNames.length > 0) || defaultProperties != null) {
                        sb.append("<p>Resource specific properties:</p>");
                    } else {
                        sb.append("<p>No resource specific properties!</p>");
                    }

                    if (propertyNames != null) {
                        for (int i = 0; i < propertyNames.length; i++) {
                            sb.append(propertyNames[i] + ": ");
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
                        }
                    }
                    if (defaultProperties != null) {
                        for (int i = 0; i < defaultProperties.length; i++) {
                            sb.append("<p>Default property: " + defaultProperties[i] + "</p>");
                            sb.append("<input type=\"hidden\" name=\"rp." + defaultProperties[i].getName() + "\" value=\"" + defaultProperties[i].getValue() + "\"/><br/>");
                        }
                    }

                    //sb.append("<br/><br/><input type=\"submit\" value=\"Save As\" name=\"save-as\"/>");

		    sb.append("<br/><br/>Save as:<br/>");
		    sb.append("Look in: " + getPath() + "<br/>");
                    // TODO: Display repository navigation of this path ...
                    Sitetree sitetree = (Sitetree) getYanel().getBeanFactory().getBean("nav-sitetree");
                    Node node = sitetree.getNode(getRealm(), getPath());
                    if (node.isCollection()) {
                        log.error("DEBUG: Is Collection: " + node.getName());
                    } else if (node.isResource()) {
                        log.error("DEBUG: Is Resource: " + node.getName());
                    } else {
                        log.error("Neither collection nor resource: " + getPath());
                    }
		    sb.append("<table border=\"1\">");
		    sb.append("<tr><th align=\"left\">Name</th><th align=\"left\">Resource Type</th></tr>");
		    sb.append("<tr><td>Collection: foo2</td><td>bar</td></tr>");
		    sb.append("<tr><td>Collection: foo1</td><td>bar</td></tr>");
		    sb.append("<tr><td>Resource: foo.html</td><td>Wiki</td></tr>");
		    sb.append("</table><br/>");
                    String createName = getRequest().getParameter("create-name");
                    if (createName != null) {
                        sb.append("Name: <input type=\"text\" name=\"create-name\" value=\"" + createName + "\"/>");
                    } else {
                        sb.append("Name: <input type=\"text\" name=\"create-name\"/>");
                    }

                    // TODO: Display realm navigation (sitetree, topic map, ...)

                    sb.append("<br/><input type=\"submit\" value=\"Save\" name=\"save\"/>");

                    sb.append("</form>");
                }
            }
        } catch (Exception e) {
            sb.append("<p>Exception: " + e + "</p>");
            log.error(e.getMessage(), e);
        }
    }
    
    /**
     * Creates new resource
     */
    private void create() throws Exception {
        org.wyona.yanel.core.map.Realm realm = getRealm();
        Path pathOfResourceCreator = new Path(getPath());

        org.wyona.commons.io.Path parent = new org.wyona.commons.io.Path(pathOfResourceCreator.toString()).getParent();

        Path pathOfNewResource = null;
        String createName = getRequest().getParameter("create-name");
        if(parent.equals("null")) {
            // if pathOfResourceCreator is ROOT
            pathOfNewResource = new Path("/" + createName);
        } else {
            pathOfNewResource = new Path(parent + "/" + createName);
        }

        log.error("DEBUG: Path of new resource: " + pathOfNewResource);

        String rtps = getRequest().getParameter("resource-type");
        String resNamespace = rtps.substring(0, rtps.indexOf("::"));
        String resName = rtps.substring(rtps.indexOf("::") + 2);
        Resource newResource = yanel.getResourceManager().getResource(request, response, realm, pathOfNewResource.toString(), new ResourceConfiguration(resName, resNamespace, null));

        if (newResource != null) {
            if (ResourceAttributeHelper.hasAttributeImplemented(newResource, "Creatable", "2")) {
                ((CreatableV2) newResource).create(request);
                createConfiguration(newResource);
            } else {
                throw new Exception("creation NOT successfull!");
            }
        }
    }

    /**
     * Create resource configuration (yanel-rti respectively yanel-rc)
     */
    private void createConfiguration(Resource newResource) throws Exception {
        StringBuffer rtiContent = new StringBuffer(newResource.getResourceTypeUniversalName() + "\n");
        java.util.HashMap rtiProperties = ((CreatableV2) newResource).createRTIProperties(request);
        if (rtiProperties != null) {
            log.error("DEBUG: " + rtiProperties + " " + PathUtil.getRTIPath(newResource.getPath()));
            java.util.Iterator iterator = rtiProperties.keySet().iterator();
            while (iterator.hasNext()) {
                String property = (String) iterator.next();
                String value = (String) rtiProperties.get(property);
                rtiContent.append(property + ": " + value + "\n");
                log.error("DEBUG: " + property + ", " + value);
            }
        } else {
            log.warn("No RTI properties: " + newResource.getPath());
        }
        org.wyona.yarep.core.Repository rcRepo = newResource.getRealm().getRTIRepository();
        org.wyona.commons.io.Path newRTIPath = new org.wyona.commons.io.Path(PathUtil.getRTIPath(newResource.getPath()));
        if (rcRepo.existsNode(newRTIPath.toString())) {
            // TODO: create node recursively ...
            rcRepo.getRootNode().addNode(newRTIPath.getName(), org.wyona.yarep.core.NodeType.RESOURCE);
	    log.warn("Node has been created: " + newRTIPath);
        }
        java.io.Writer writer = new java.io.OutputStreamWriter(rcRepo.getNode(newRTIPath.toString()).getOutputStream());
        writer.write(rtiContent.toString());
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
                ResourceTypeDefinition[] rtds = new ResourceTypeDefinition[resourceTypeConfigs.length];
                for (int i = 0; i < resourceTypeConfigs.length; i++) {
                    try {
                        if (resourceTypeConfigs[i].getAttribute("namespace").equals(resNamespace) && resourceTypeConfigs[i].getAttribute("name").equals(resName)) {
                            log.error("DEBUG: Resource Type Found: " + resName + ", " + resNamespace);
                            Configuration[] propertyConfigs = resourceTypeConfigs[i].getChildren("property");
                            Property[] props = new Property[propertyConfigs.length];
                            for (int k = 0; k < propertyConfigs.length; k++) {
                                props[k] = new Property(propertyConfigs[k].getAttribute("name"), propertyConfigs[k].getAttribute("value"));
                                log.error("DEBUG: Property: " + props[k]);
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
