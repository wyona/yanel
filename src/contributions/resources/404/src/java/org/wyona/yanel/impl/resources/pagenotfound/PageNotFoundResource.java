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
        sb.append("<y:yanel xmlns:y=\"http://www.wyona.org/yanel/1.0\" servlet-context-real-path=\"TBD\">");
        sb.append("<y:request y:servlet-path=\"" + getPath() + "\" y:uri=\"" + getPath() + "\">TBD</y:request>");
        sb.append("<y:session y:id=\"TBD\">TBD</y:session>");
        sb.append("<y:resource>");
        sb.append("<y:config y:rti-name=\"TBD\" y:rti-namespace=\"TBD\"/>");
        sb.append("<y:realm/>");
        sb.append("<y:view/>");
        sb.append("<y:size/>");
        sb.append("</y:resource>");
        sb.append("<y:exception y:status=\"404\">");
        sb.append("Resource not found exception: " + getPath());
        sb.append("</y:exception>");
        sb.append("</y:yanel>");
        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
