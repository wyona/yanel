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
public class ResourceCreatorResource extends Resource implements ViewableV2{

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
        view.setMimeType("text/plain");
        view.setInputStream(new java.io.StringBufferInputStream("Hello World"));
        return view;
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        ViewDescriptor[] vd = new ViewDescriptor[2];
        vd[0] = new ViewDescriptor();
        vd[0].setMimeType("application/xhtml+xml");
        vd[1] = new ViewDescriptor();
        vd[1].setMimeType("application/xml");
        return vd;
    }
}
