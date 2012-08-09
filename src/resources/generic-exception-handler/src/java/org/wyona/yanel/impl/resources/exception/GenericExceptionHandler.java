/*
 * Copyright 2011 Wyona
 */

package org.wyona.yanel.impl.resources.exception;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.lang.Throwable;
import java.lang.StackTraceElement;

import org.apache.log4j.Logger;

/**
 * A generic exception handler.
 */
public class GenericExceptionHandler extends org.wyona.yanel.impl.resources.BasicGenericExceptionHandlerResource {
    private static Logger log = Logger.getLogger(GenericExceptionHandler.class);

    /**
     * Generate XML
     * @param viewid Ignored.
     * @see org.wyona.yanel.core.Resource
     */
/*
    @Override
    protected InputStream getContentXML(String viewId) {
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<yanel xmlns=\"http://www.wyona.org/yanel/1.0\">");

        // Request information
        sb.append("<request servlet-path=\"");
        sb.append(getPath());
        sb.append("\" uri=\"");
        sb.append(getPath());
        sb.append("\"/>");
        sb.append("</yanel>");

        return new ByteArrayInputStream(sb.toString().getBytes());
    }
*/
}
