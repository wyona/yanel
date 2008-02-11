/*
 * Copyright 2007 Wyona
 */

package bar.foo.yanel.impl.resources;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;


/**
 * A simple Resource which extends BasicXMLResource
 */
public class FromScratchResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(FromScratchResource.class);
    
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
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<root>");
 
        sb.append("</root>");
        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
