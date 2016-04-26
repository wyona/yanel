/*
 * Copyright 2016 Wyona
 */
package org.wyona.yanel.resources.revisions;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Revisions of a particular resource
 */
public class RevisionsResource extends BasicXMLResource {
    
    private static Logger log = LogManager.getLogger(RevisionsResource.class);
    
    /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    @Override
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<revisions/>");

        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
