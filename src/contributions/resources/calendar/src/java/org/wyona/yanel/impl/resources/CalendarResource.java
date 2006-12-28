/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.apache.log4j.Category;

/**
 *
 */
public class CalendarResource extends Resource implements ViewableV2 {

    private static Category log = Category.getInstance(CalendarResource.class);

    /**
     *
     */
    public CalendarResource() {
    }

    /**
     *
     */
    public long getSize() throws Exception {
        log.warn("Not implemented yet!");
        return -1;
    }

    /**
     *
     */
    public boolean exists() throws Exception {
        log.warn("Not implemented yet!");
        return false;
    }

    /**
     *
     */
    public View getView(String viewId) throws Exception {
        org.wyona.yarep.core.Repository dataRepo = getRealm().getRepository();
        org.wyona.yarep.core.Path[] children = dataRepo.getChildren(new org.wyona.yarep.core.Path(getPath().toString()));

        StringBuffer calendar = new StringBuffer("<?xml version=\"1.0\"?>\n<calendar>");
        for (int i = 0; i < children.length; i++) {
            log.error("DEBUG: Child: " + children[i]);
            if (dataRepo.isResource(children[i])) {
                java.io.InputStream in = dataRepo.getInputStream(children[i]);
                StringBuffer event = new StringBuffer();
            
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    event.append(new String(buffer));
                }

                log.error("DEBUG: Event: " + event);
                int endOfProcessingInstruction = event.indexOf("?>");
                if (endOfProcessingInstruction > 0) {
                    calendar.append(event.toString().substring(endOfProcessingInstruction + 2));
                } else {
                    log.error("No processing instruction: " + children[i]);            
                }
            }
        }
        calendar.append("</calendar>");

        //response.getOutputStream();

        View view = new View();
        //view.setResponse(false);
	view.setMimeType("application/xml");
	view.setInputStream(new java.io.StringBufferInputStream(calendar.toString()));
        return view;
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        log.warn("Not implemented yet!");
        return null;
    }
}
