/*
 * Copyright 2008 Wyona
 */

package org.wyona.yanel.impl.resources.sessionmanager;

import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.yanel.servlet.IdentityMap;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Session manager resource
 */
public class SessionManagerResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(SessionManagerResource.class);
    
    /**
     *
     */
    protected InputStream getContentXML(String viewId) {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<session-manager xmlns=\"http://www.wyona.org/yanel/1.0\">");
        javax.servlet.http.HttpSession[] activeSessions = org.wyona.yanel.servlet.SessionCounter.getActiveSessions();
        sb.append("<number-of-sessions>" + activeSessions.length + "</number-of-sessions>");

        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSZ");
        for (int i = 0; i < activeSessions.length; i++) {
            try {
                sb.append("<session id=\"" + activeSessions[i].getId() + "\" creation-time=\"" + dateFormat.format(new Date(activeSessions[i].getCreationTime())) + "\" last-accessed-time=\"" + dateFormat.format(new Date(activeSessions[i].getLastAccessedTime())) + "\">");
                IdentityMap identityMap = (IdentityMap) activeSessions[i].getAttribute(org.wyona.yanel.servlet.YanelServlet.IDENTITY_MAP_KEY);
                if (identityMap != null) {
                    sb.append("<identities>" + identityMap.toString() + "</identities>");
                } else {
                    sb.append("<no-identity-yet/>");
                }
                sb.append("</session>");
            } catch (Exception e) {
                sb.append("<exception session-id=\"" + activeSessions[i].getId() + "\">" + e.getMessage() + "</exception>");
            }
        }
        sb.append("</session-manager>");
        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
