/*
 * Copyright 2011 Wyona
 */

package org.wyona.yanel.impl.resources;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import org.wyona.commons.xml.XMLHelper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.lang.Throwable;
import java.lang.StackTraceElement;

import org.apache.log4j.Logger;

/**
 * A generic exception handler.
 */
public class BasicGenericExceptionHandlerResource extends BasicXMLResource {
    private static Logger log = Logger.getLogger(BasicGenericExceptionHandlerResource.class);

    private Exception e;

    /**
     *
     */
    public void setException(Exception e) {
        this.e = e;
    }

    /**
     *
     */
    public Exception getException() {
        return e;
    }

    /**
     * Generate XML
     * @param viewid Ignored.
     * @see org.wyona.yanel.core.Resource
     */
    @Override
    protected InputStream getContentXML(String viewId) throws Exception {
        String NAMESPACE = "http://www.wyona.org/yanel/1.0";

        org.w3c.dom.Document doc = XMLHelper.createDocument(NAMESPACE, "yanel");

        org.w3c.dom.Element rootElement = doc.getDocumentElement();

        org.w3c.dom.Element requestElement = doc.createElementNS(NAMESPACE, "request");
        requestElement.setAttributeNS(NAMESPACE, "servlet-path", getPath());
        requestElement.setAttributeNS(NAMESPACE, "uri", getPath());
        rootElement.appendChild(requestElement);

        if (e != null) {
            log.error(e, e);
            org.w3c.dom.Element exceptionElement = doc.createElementNS(NAMESPACE, "exception");
            exceptionElement.setAttributeNS(NAMESPACE, "message", e.getMessage());
            rootElement.appendChild(exceptionElement);
            exceptionElement.appendChild(doc.createTextNode(getStackTrace(e)));
        } else {
            log.error("No exception has been set!");
        }

        return XMLHelper.getInputStream(doc, false, true, null);
    }

    /**
     * Get stack trace of exception
     * @param e The exception to handle.
     */
    private String getStackTrace(Exception e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        e.printStackTrace(new java.io.PrintWriter(sw));
        return sw.toString();
    }
}
