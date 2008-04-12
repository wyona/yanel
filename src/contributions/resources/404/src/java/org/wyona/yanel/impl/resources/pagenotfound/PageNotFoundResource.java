/*
 * Copyright 2008 Wyona
 */

package org.wyona.yanel.impl.resources.pagenotfound;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;


/**
 * Page not found resource
 */
public class PageNotFoundResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(PageNotFoundResource.class);
    
    /**
     * Generate XML
     */
    protected InputStream getContentXML(String viewId) {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<yanel xmlns=\"http://www.wyona.org/yanel/1.0\" servlet-context-real-path=\"TBD\">");
        sb.append("<request>TBD</request>");
        sb.append("<session>TBD</session>");
        sb.append("<resource>TBD</resource>");
        sb.append("<exception status=\"404\">");
        sb.append("Resource not found exception: " + getPath());
        sb.append("</exception>");
        sb.append("</yanel>");
        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
