/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceTypeDefinition;
import org.wyona.yanel.core.ResourceTypeRegistry;
import org.wyona.yanel.core.api.attributes.CreatableV2;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.util.ResourceAttributeHelper;

import org.apache.log4j.Category;

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
     *
     */
    private String getScreen() {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        sb.append("<body>");

        javax.servlet.http.HttpServletRequest request = getRequest();
        java.util.Enumeration parameters = request.getParameterNames();
        if (!parameters.hasMoreElements()) {
            getSelectResourceTypeScreen(sb);
        } else {
            if (request.getParameter("resource-type") != null) {
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
                    sb.append("<h2>Enter/Select resource specific parameters</h2>");
                    sb.append("<p>Resource Type: " + resName + " ("+resNamespace+")</p>");
                    sb.append("<form>");

                    if (propertyNames != null && propertyNames.length > 0) {
                        sb.append("<p>Resource specific properties:</p>");
                        for (int i = 0; i < propertyNames.length; i++) {
                            sb.append(propertyNames[i] + ": <input name=\"" + propertyNames[i]
                                + "\" value=\""
                                + ((CreatableV2) resource).getProperty(propertyNames[i])
                                + "\" size=\"60\"/><br/>");
                        }
                    } else {
                        sb.append("<p>No resource specific properties!</p>");
                    }

                    sb.append("<br/><br/><input type=\"submit\" value=\"Save As\" name=\"save.as\"/>");
                    sb.append("</form>");
                }
            }
        } catch (Exception e) {
            sb.append("<p>Exception: "+e+"</p>");
            log.error(e);
        }
    }
}
