/*
 * Copyright 2012 Wyona
 */
package org.wyona.yanel.impl.resources.cas;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * TODO
 */
public class CASProxyCallbackResource extends BasicXMLResource {
    
    private static Logger log = LogManager.getLogger(CASProxyCallbackResource.class);
    
    /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    @Override
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        String pgtId = getEnvironment().getRequest().getParameter("pgtId"); // INFO: http://www.jusfortechies.com/java/cas/protocol.php#tgt
        String pgtIou = getEnvironment().getRequest().getParameter("pgtIou"); // INFO: http://www.jusfortechies.com/java/cas/protocol.php#pgt-iou
        if (pgtId != null && pgtIou != null) {
            log.warn("DEBUG: pgt Id: " + pgtId + ", pgt Iou: " + pgtIou);
            java.io.File proxyIdFile = new java.io.File(System.getProperty("java.io.tmpdir"), org.wyona.yanel.servlet.security.impl.CASWebAuthenticatorImpl.getProxyIdFilename(pgtIou));
            java.io.FileOutputStream out = new java.io.FileOutputStream(proxyIdFile);
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
