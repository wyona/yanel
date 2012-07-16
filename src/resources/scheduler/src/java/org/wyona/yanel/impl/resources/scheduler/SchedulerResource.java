/*
 * Copyright 2012 Wyona
 */

package org.wyona.yanel.impl.resources.scheduler;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * Scheduler jobs overview
 */
public class SchedulerResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(SchedulerResource.class);
    
    /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    @Override
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<scheduler>");
 
        sb.append("</scheduler>");
        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
