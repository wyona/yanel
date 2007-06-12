/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.navigation.Node;
import org.wyona.yanel.core.navigation.Sitetree;

import org.apache.log4j.Category;

/**
 *
 */
public class DataRepoSitetreeResource extends Resource implements ViewableV2 {

    private static Category log = Category.getInstance(DataRepoSitetreeResource.class);

    /**
     *
     */
    public DataRepoSitetreeResource() {
    }

    /**
     *
     */
    public String getMimeType(String viewId) throws Exception {
        if (viewId != null && viewId.equals("xml")) return "application/xml";
        return "application/xhtml+xml";
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
    public View getView(String viewId) throws Exception {

        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        if (viewId != null && viewId.equals("xml")) {
            sb.append(getSitetreeAsXML());
            //sb.append(getSitetreeAsXML(getPath().toString()));
        } else {
            sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:xhtml=\"http://www.w3.org/1999/xhtml\"><head><title>Browse Data Repository Sitetree</title></head><body><a href=\"?yanel.resource.viewid=xml\">Show XML</a></body></html>");
        }

        View view = new View();
        view.setMimeType(getMimeType(viewId));
        view.setInputStream(new java.io.StringBufferInputStream(sb.toString()));
        return view;
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        try {
            ViewDescriptor[] vd = new ViewDescriptor[2];
            vd[0] = new ViewDescriptor("default");
            vd[0].setMimeType(getMimeType(null));
            vd[1] = new ViewDescriptor("xml");
            vd[1].setMimeType(getMimeType("xml"));
            return vd;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     *
     */
    private String getSitetreeAsXML() {
    //private String getSitetreeAsXML(String path) {
        String path = "/"; 
        StringBuffer sb = new StringBuffer("<sitetree>");
        Sitetree sitetree = (Sitetree) getYanel().getBeanFactory().getBean("repo-navigation");
        Node node = sitetree.getNode(getRealm(), path);
        if (node.isCollection()) {
            sb.append("<collection path=\"" + path + "\">");
            Node[] children = node.getChildren();
            for (int i = 0; i < children.length; i++) {
                if (children[i].isCollection()) {
                    sb.append("<collection path=\"" + children[i].getPath() + "\"/>");
                } else if (children[i].isResource()) {
                    sb.append("<resource path=\"" + children[i].getPath() + "\"/>");
                } else {
                    sb.append("<neither-resource-nor-collection path=\"" + children[i].getPath() + "\"/>");
                }
            }
            sb.append("</collection>");
        } else {
            sb.append("<resource path=\"" + path + "\"/>");
        }
        sb.append("</sitetree>");
        return sb.toString();
    }
}
