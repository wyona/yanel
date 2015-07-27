/*
 * Copyright 2015 Wyona
 */
package org.wyona.yanel.impl.resources.login;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.wyona.commons.xml.XMLHelper;

/**
 * Login resource
 */
public class LoginResource extends BasicXMLResource {
    
    private static Logger log = LogManager.getLogger(LoginResource.class);
    
    /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    @Override
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        org.w3c.dom.Document doc = XMLHelper.createDocument(org.wyona.yanel.servlet.YanelServlet.NAMESPACE, "yanel-auth-screen");

        return XMLHelper.getInputStream(doc, false, true, null);
    }
}
