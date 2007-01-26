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
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.core.util.ResourceAttributeHelper;

import org.apache.log4j.Category;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

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
    public View getView(String viewId) {
        View view = new View();
        view.setMimeType("application/xhtml+xml");
        view.setInputStream(new java.io.StringBufferInputStream(getScreen()));
        return view;
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        ViewDescriptor[] vd = new ViewDescriptor[2];
        vd[0] = new ViewDescriptor("default");
        vd[0].setMimeType("application/xhtml+xml");
        vd[1] = new ViewDescriptor("source");
        vd[1].setMimeType("application/xml");
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
        sb.append("Resource Type: <select name=\"resource-type\">");

        ResourceTypeRegistry rtr = new ResourceTypeRegistry();
        ResourceTypeDefinition[] rtds = rtr.getResourceTypeDefinitions();
        for (int i = 0; i < rtds.length; i++) {
            try {
                Resource resource = rtr.newResource(rtds[i].getResourceTypeUniversalName());
                if (resource != null && ResourceAttributeHelper.hasAttributeImplemented(resource, "Creatable", "2")) {
                    sb.append("<option value=\"" + rtds[i].getResourceTypeNamespace() + "::" + rtds[i].getResourceTypeLocalName() + "\">" + rtds[i].getResourceTypeLocalName() + "</option>");
                }
            } catch(Exception e) {
                log.error(e);
            }
        }

        sb.append("</select>");
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
                    String[] propertyNames = ((CreatableV2) resource).getPropertyNames();
                    //((CreatableV2) resource).setProperty("Name", createName);

                    sb.append("<h4>Create resource (step 2)</h4>");
                    sb.append("<h2>Enter/Select resource specific parameters and \"Save As\"</h2>");
                    sb.append("<p>Resource Type: " + resName + " ("+resNamespace+")</p>");
                    sb.append("<form>");
                    // TODO: Add this parameter to the continuation within the session!
                    sb.append("<input type=\"hidden\" name=\"resource-type\" value=\""+rtps+"\"/>");

                    if (propertyNames != null && propertyNames.length > 0) {
                        sb.append("<p>Resource specific properties:</p>");
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
                    } else {
                        sb.append("<p>No resource specific properties!</p>");
                    }

                    //sb.append("<br/><br/><input type=\"submit\" value=\"Save As\" name=\"save-as\"/>");

		    sb.append("<br/><br/>Save as:<br/>");
                    String createName = getRequest().getParameter("create-name");
                    if (createName != null) {
                        sb.append("Name: <input type=\"text\" name=\"create-name\" value=\"" + createName + "\"/>");
                    } else {
                        sb.append("Name: <input type=\"text\" name=\"create-name\"/>");
                    }
                    sb.append("<br/><input type=\"submit\" value=\"Save\" name=\"save\"/>");

                    sb.append("</form>");
                }
            }
        } catch (Exception e) {
            sb.append("<p>Exception: "+e+"</p>");
            log.error(e);
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
     *
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
                    java.io.Writer writer = newResource.getRealm().getRTIRepository().getWriter(new org.wyona.yarep.core.Path(PathUtil.getRTIPath(newResource.getPath())));
                    writer.write(rtiContent.toString());
                    writer.close();
    }
}
