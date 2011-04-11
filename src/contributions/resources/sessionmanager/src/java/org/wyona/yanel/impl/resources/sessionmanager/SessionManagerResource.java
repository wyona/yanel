/*
 * Copyright 2008 Wyona
 */

package org.wyona.yanel.impl.resources.sessionmanager;

import org.wyona.yanel.servlet.YanelServlet;
import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.yanel.servlet.IdentityMap;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Session manager resource
 */
public class SessionManagerResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(SessionManagerResource.class);
    
    private String NAMESPACE = "http://www.wyona.org/yanel/1.0";

    /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    @Override
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        Document doc = org.wyona.commons.xml.XMLHelper.createDocument(NAMESPACE, "session-manager");
        Element rootEl = doc.getDocumentElement();

        //StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        //sb.append("<session-manager xmlns=\"http://www.wyona.org/yanel/1.0\">");

        javax.servlet.http.HttpSession[] activeSessions = org.wyona.yanel.servlet.SessionCounter.getActiveSessions();
        Element numberOfSessionsEl = doc.createElementNS(NAMESPACE, "number-of-sessions");
        numberOfSessionsEl.appendChild(doc.createTextNode("" + activeSessions.length));
        rootEl.appendChild(numberOfSessionsEl);
        //sb.append("<number-of-sessions>" + activeSessions.length + "</number-of-sessions>");

        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSZ");
        for (int i = 0; i < activeSessions.length; i++) {
            try {
                Element sessionEl = doc.createElementNS(NAMESPACE, "session");
                sessionEl.setAttribute("id", activeSessions[i].getId());
                sessionEl.setAttribute("creation-time", dateFormat.format(new Date(activeSessions[i].getCreationTime())));
                sessionEl.setAttribute("last-accessed-time", dateFormat.format(new Date(activeSessions[i].getLastAccessedTime())));
                //sb.append("<session id=\"" + activeSessions[i].getId() + "\" creation-time=\"" + dateFormat.format(new Date(activeSessions[i].getCreationTime())) + "\" last-accessed-time=\"" + dateFormat.format(new Date(activeSessions[i].getLastAccessedTime())) + "\">");
                rootEl.appendChild(sessionEl);

                // TODO ...
                IdentityMap identityMap = (IdentityMap) activeSessions[i].getAttribute(org.wyona.yanel.servlet.YanelServlet.IDENTITY_MAP_KEY);
                if (identityMap != null) {
                    Element identitiesEl = doc.createElementNS(NAMESPACE, "identities");
                    identitiesEl.appendChild(doc.createTextNode(identityMap.toString()));
                    sessionEl.appendChild(identitiesEl);
                    //sb.append("<identities>" + identityMap.toString() + "</identities>");
                } else {
                    sessionEl.appendChild(doc.createElementNS(NAMESPACE, "no-identity-yet"));
                    //sb.append("<no-identity-yet/>");
                }
                String lastAccessedURL = (String) activeSessions[i].getAttribute(YanelServlet.YANEL_LAST_ACCESS_ATTR);
                if(lastAccessedURL != null) {
                    Element lastAccessedURLEl = doc.createElementNS(NAMESPACE, "last-accessed-url");
                    lastAccessedURLEl.appendChild(doc.createTextNode(lastAccessedURL));
                    sessionEl.appendChild(lastAccessedURLEl);
                    //sb.append("<last-accessed-url>" + lastAccessedURL + "</last-accessed-url>");
                }
                //sb.append("</session>");
            } catch (Exception e) {
                Element exceptionEl = doc.createElementNS(NAMESPACE, "exception");
                exceptionEl.setAttribute("session-id", activeSessions[i].getId());
                exceptionEl.appendChild(doc.createTextNode(e.getMessage()));
                rootEl.appendChild(exceptionEl);
                //sb.append("<exception session-id=\"" + activeSessions[i].getId() + "\">" + e.getMessage() + "</exception>");
            }
        }
        //sb.append("</session-manager>");
        //return new ByteArrayInputStream(sb.toString().getBytes());
        return org.wyona.commons.xml.XMLHelper.getInputStream(doc, false, false, null);
    }
}
