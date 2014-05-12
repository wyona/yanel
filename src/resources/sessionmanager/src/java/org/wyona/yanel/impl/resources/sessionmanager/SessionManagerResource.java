/*
 * Copyright 2008 Wyona
 */

package org.wyona.yanel.impl.resources.sessionmanager;

import org.wyona.yanel.servlet.YanelServlet;
import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.yanel.servlet.CASTicketsMap;
import org.wyona.yanel.servlet.IdentityMap;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.codec.binary.Hex;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Session manager resource
 */
public class SessionManagerResource extends BasicXMLResource {
    
    private static Logger log = LogManager.getLogger(SessionManagerResource.class);
    
    private String NAMESPACE = "http://www.wyona.org/yanel/1.0";

    private boolean hash = true;

    /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    @Override
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        if (getResourceConfigProperty("hash") != null) {
            hash = Boolean.parseBoolean(getResourceConfigProperty("hash"));
        } else {
            hash = true;
        }

        Document doc = org.wyona.commons.xml.XMLHelper.createDocument(NAMESPACE, "session-manager");
        Element rootEl = doc.getDocumentElement();

        javax.servlet.http.HttpSession[] activeSessions = org.wyona.yanel.servlet.SessionCounter.getActiveSessions();
        Element numberOfSessionsEl = doc.createElementNS(NAMESPACE, "number-of-sessions");
        numberOfSessionsEl.appendChild(doc.createTextNode("" + activeSessions.length));
        rootEl.appendChild(numberOfSessionsEl);

        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSZ");
        for (int i = 0; i < activeSessions.length; i++) {
            // WARN/TODO: Only append sessions associated with this realm
            try {
                Element sessionEl = doc.createElementNS(NAMESPACE, "session");
                sessionEl.setAttribute("id", hashSessionID(activeSessions[i].getId()));
                sessionEl.setAttribute("creation-time", dateFormat.format(new Date(activeSessions[i].getCreationTime())));
                sessionEl.setAttribute("last-accessed-time", dateFormat.format(new Date(activeSessions[i].getLastAccessedTime())));
                rootEl.appendChild(sessionEl);

                CASTicketsMap casTicketsMap = (CASTicketsMap) activeSessions[i].getAttribute(org.wyona.yanel.servlet.security.impl.CASWebAuthenticatorImpl.CAS_TICKETS_SESSION_NAME);
                if (casTicketsMap != null) {
                    Element casTicketEl = doc.createElementNS(NAMESPACE, "cas-ticket");
                    casTicketEl.appendChild(doc.createTextNode(hashSessionID(casTicketsMap.toString())));
                    sessionEl.appendChild(casTicketEl);
                } else {
                }

                String casProxyTicket = (String) activeSessions[i].getAttribute(org.wyona.yanel.servlet.security.impl.CASWebAuthenticatorImpl.CAS_PROXY_TICKET_SESSION_NAME);
                if (casProxyTicket != null) {
                    Element casProxyTicketEl = doc.createElementNS(NAMESPACE, "cas-proxy-ticket");
                    casProxyTicketEl.appendChild(doc.createTextNode(hashSessionID(casProxyTicket)));
                    sessionEl.appendChild(casProxyTicketEl);
                } else {
                }

                IdentityMap identityMap = (IdentityMap) activeSessions[i].getAttribute(org.wyona.yanel.servlet.YanelServlet.IDENTITY_MAP_KEY);
                if (identityMap != null) {
                    Element identitiesEl = doc.createElementNS(NAMESPACE, "identities");
                    identitiesEl.appendChild(doc.createTextNode(identityMap.toString()));
                    sessionEl.appendChild(identitiesEl);
                } else {
                    sessionEl.appendChild(doc.createElementNS(NAMESPACE, "no-identity-yet"));
                }
                String lastAccessedURL = (String) activeSessions[i].getAttribute(YanelServlet.YANEL_LAST_ACCESS_ATTR);
                if(lastAccessedURL != null) {
                    Element lastAccessedURLEl = doc.createElementNS(NAMESPACE, "last-accessed-url");
                    lastAccessedURLEl.appendChild(doc.createTextNode(lastAccessedURL));
                    sessionEl.appendChild(lastAccessedURLEl);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                Element exceptionEl = doc.createElementNS(NAMESPACE, "exception");
                exceptionEl.setAttribute("session-id", hashSessionID(activeSessions[i].getId()));
                exceptionEl.appendChild(doc.createTextNode(e.getMessage()));
                rootEl.appendChild(exceptionEl);
            }
        }
        return org.wyona.commons.xml.XMLHelper.getInputStream(doc, false, false, null);
    }

    /**
     * Hash session ID in order to prevent session hijacking (http://en.wikipedia.org/wiki/Session_hijacking)
     * @param id Real session ID
     */
    private String hashSessionID(String id) {
        if (!hash) {
            return id;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(id.getBytes("UTF-8"));
            return Hex.encodeHexString(digest);
        } catch(UnsupportedEncodingException e) {
            // The spec guarantees us that UTF-8 is available.
            log.error(e, e);
        } catch(NoSuchAlgorithmException e) {
            // The spec guarantees us that SHA-1 is available.
            log.error(e, e);
        }

        return "(err)";
    }
}
