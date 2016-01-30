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
 * Process proxy ticket requests of CAS and make it available for user session (see org.wyona.yanel.servlet.security.impl.CASWebAuthenticatorImpl)
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
            setPgtId(pgtId, pgtIou);
        } else {
            log.warn("No parameter 'pgtId' or 'pgtIou' received ('" + getEnvironment().getRequest().getRequestURL() + "', '" + getEnvironment().getRequest().getQueryString() + "')!");
        }

        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<root/>");

        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    /**
     * Write ticket into shared repository, such that CASWebAuthenticatorImpl can access it
     * @param pgtId 'TGT-2-Q2HIIavaNe4Dom6UDQ7As1zR6Td79SwScffCC6dD7XKDZRXNBm-cas01.example.org'
     * @param pgtIou 'PGTIOU-1-hG9ive0rfjuTb9IHaRsn-cas01.example.org'
     */
    private void setPgtId(String pgtId, String pgtIou) throws Exception {
        log.warn("DEBUG: pgt Id: " + pgtId + ", pgt Iou: " + pgtIou);

        String nodePath = org.wyona.yanel.servlet.security.impl.CASWebAuthenticatorImpl.getProxyIdNodePath(pgtIou);
        if (!getRealm().getRepository().existsNode(nodePath)) {
            org.wyona.yarep.core.Node proxyIdNode = org.wyona.yarep.util.YarepUtil.addNodes(getRealm().getRepository(), nodePath, org.wyona.yarep.core.NodeType.RESOURCE);
            java.io.OutputStream out = proxyIdNode.getOutputStream();
            out.write(pgtId.getBytes());
            out.close();

            // TODO: Delete all tickets older than one day (or make it configurable)
        } else {
            log.error("Node '" + nodePath + "' already exists!");
        }
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#exists()
     */
    @Override
    public boolean exists() throws Exception {
        return true;
    }
}
