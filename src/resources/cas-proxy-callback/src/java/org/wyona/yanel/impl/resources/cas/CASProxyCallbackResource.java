/*
 * Copyright 2012 Wyona
 */
package org.wyona.yanel.impl.resources.cas;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * A simple Resource which extends BasicXMLResource
 */
public class CASProxyCallbackResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(CASProxyCallbackResource.class);
    
    /**
     * This method overrides the method to create the InputStream called by BasicXMLResource
     * Since you extend the BasicXMLResource this has to contain well-formed xml.
     * Should return a InputStream which contains XML. 
     * Use StringBuilder, dom, jdom, org.apache.commons.io.IOUtils and so on to generate the XML.
     *
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    @Override
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        String pgtId = getEnvironment().getRequest().getParameter("pgtId");
        if (pgtId != null) {
            log.warn("DEBUG: pgt Id: " + pgtId);
            java.io.FileOutputStream out = new java.io.FileOutputStream("/Users/michaelwechner/pgt-id.txt");
            out.write(pgtId.getBytes());
            out.close();
        } else {
            log.warn("No pgt Id!");
        }

        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<root/>");

        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
