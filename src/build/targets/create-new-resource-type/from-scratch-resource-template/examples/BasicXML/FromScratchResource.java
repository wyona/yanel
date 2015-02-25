/*
 * Copyright 2015 Wyona
 */

package bar.foo.yanel.impl.resources;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * A simple HelloWorldResource which extends BasicXMLResource to output "Hello World!" in XML
 */
public class FromScratchResource extends BasicXMLResource {
    
    private static Logger log = LogManager.getLogger(FromScratchResource.class);
    
    /**
     * This method overrides the method to create the InputStream called by BasicXMLResource
     * Since you extend the BasicXMLResource this has to contain well-formed xml.
     * Beside the simple StringBuffer there are several other ways how to construct the xml. see dom, jdom, org.apache.commons.io.IOUtils and so on
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<root>");
        sb.append("<child>");
        sb.append("Hello World!");
        sb.append("</child>");
        sb.append("</root>");
        return new ByteArrayInputStream(sb.toString().getBytes("utf-8"));
    }

}
