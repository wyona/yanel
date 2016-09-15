/*
 * Copyright 2016 Wyona
 */
package bar.foo.yanel.impl.resources;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * A simple Resource which extends BasicXMLResource
 */
public class FromScratchResource extends BasicXMLResource {
    
    private static Logger log = LogManager.getLogger(FromScratchResource.class);
    
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

        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<root/>");

        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
