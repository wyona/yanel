/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceTypeDefinition;
import org.wyona.yanel.core.ResourceTypeRegistry;
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
        sb.append("<p>Select resource type:</p>");
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
        sb.append("<p>Resource Type: " + getRequest().getParameter("resource-type") + "</p>");
    }
}
