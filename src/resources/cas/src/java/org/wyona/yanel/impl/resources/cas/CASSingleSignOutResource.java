/*
 * Copyright 2012 Wyona
 */
package org.wyona.yanel.impl.resources.cas;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Logout user from Yanel when Yanel gets notified by CAS about single sign out
 */
public class CASSingleSignOutResource extends BasicXMLResource {
    
    private static Logger log = LogManager.getLogger(CASSingleSignOutResource.class);
    
    /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    @Override
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        log.warn("DEBUG: Logout user 'TODO' ...");
        // See src/webapp/src/java/org/wyona/yanel/servlet/YanelServlet.java#doCASLogout(...)

        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<root/>");

        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
