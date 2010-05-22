/*
 * Copyright 2009 Wyona
 */

package org.wyona.yanel.impl.resources.reloadrealms;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;


/**
 * A simple Resource which extends BasicXMLResource
 */
public class ReloadRealmsResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(ReloadRealmsResource.class);
    
    /*
     * This method overrides the method to create the InputStream called by BasicXMLResource
     * Since you extend the BasicXMLResource this has to contain well-formed xml.
     * Should return a InputStream which contains XML. 
     * Use String, StingBuffer, dom, jdom, org.apache.commons.io.IOUtils and so on to generate the XML.
     */
    protected InputStream getContentXML(String viewId) {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<reload-realms>");
        try {
            org.wyona.yanel.core.map.RealmManager rm = getYanel().getRealmConfiguration();
            rm.readRealms(new java.io.File(rm.getRealmsConfigurationFile()));
        } catch(Exception e) {
            log.error(e);
            sb.append("<exception>" + e.getMessage() + "</exception>");
        }
        sb.append("</reload-realms>");
        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
