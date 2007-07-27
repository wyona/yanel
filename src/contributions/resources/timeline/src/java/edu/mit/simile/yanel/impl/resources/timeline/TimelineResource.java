/*
 * Copyright 2007 Wyona
 */

package edu.mit.simile.yanel.impl.resources.timeline;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.apache.log4j.Category;

/**
 *
 */
public class TimelineResource extends Resource implements ViewableV2 {

    private static Category log = Category.getInstance(TimelineResource.class);

    /**
     *
     */
    public TimelineResource() {
    }

    /**
     *
     */
    public boolean exists() {
        log.warn("Not really implemented yet! Needs to check if events XML exists.");
        return true;
    }

    /**
     *
     */
    public String getMimeType(String viewId) {
        if (viewId != null) {
            if (viewId.equals("xml")) return "application/xml";
        }
        return "application/xhtml+xml";
    }

    /**
     *
     */
    public long getSize() {
        log.warn("Not implemented yet!");
        return -1;
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        ViewDescriptor[] vd = new ViewDescriptor[2];
        vd[0] = new ViewDescriptor("default");
        vd[0].setMimeType(getMimeType(null));
        vd[1] = new ViewDescriptor("xml");
        vd[1].setMimeType(getMimeType("xml"));
        return vd;
    }

    /**
     *
     */
    public View getView(String viewId) {
        View view = new View();
        try {
            view.setInputStream(getRealm().getRepository().getNode("/timeline.xhtml").getInputStream());
            view.setMimeType(getMimeType(null));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return view;
    }
}
