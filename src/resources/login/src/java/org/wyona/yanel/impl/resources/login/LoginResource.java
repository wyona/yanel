/*
 * Copyright 2015 Wyona
 */
package org.wyona.yanel.impl.resources.login;

import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.yanel.servlet.YanelServlet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.wyona.commons.xml.XMLHelper;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

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

        Document doc = XMLHelper.createDocument(YanelServlet.NAMESPACE, "yanel-auth-screen");
        Element rootElement = doc.getDocumentElement();

        Element realmElement = (Element) rootElement.appendChild(doc.createElementNS(YanelServlet.NAMESPACE, "realm"));
        realmElement.setAttributeNS(YanelServlet.NAMESPACE, "name", realm.getName());
        realmElement.setAttributeNS(YanelServlet.NAMESPACE, "mount-point", realm.getMountPoint().toString());

        return XMLHelper.getInputStream(doc, false, true, null);
    }
}
