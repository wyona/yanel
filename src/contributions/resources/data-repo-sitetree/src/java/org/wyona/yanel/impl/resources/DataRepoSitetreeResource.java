/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

/**
 *
 */
public class DataRepoSitetreeResource extends Resource implements ViewableV2 {

    /**
     *
     */
    public DataRepoSitetreeResource() {
    }

    /**
     *
     */
    public String getMimeType(String viewId) throws Exception {
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
        sb.append("<sitetree/>");

        View view = new View();
        view.setMimeType("application/xml");
        view.setInputStream(new java.io.StringBufferInputStream(sb.toString()));
        return view;
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        return null;
    }
}
