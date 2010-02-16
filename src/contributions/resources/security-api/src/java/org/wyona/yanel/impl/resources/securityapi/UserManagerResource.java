/*
 * Copyright 2010 Wyona
 */

package org.wyona.yanel.impl.resources.securityapi;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * Read and write XML re UserManager
 */
public class UserManagerResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(UserManagerResource.class);
    
    /**
     *
     */
    protected InputStream getContentXML(String viewId) {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<security-api>");
        String usecase = getEnvironment().getRequest().getParameter("yanel.usecase");
        if (usecase != null) {
            log.warn("DEBUG: Yanel usecase: " + usecase);
            sb.append("<yanel-usecase>" + usecase + "</yanel-usecase>");
            if (usecase.equals("getusers")) {
                sb.append(getUsersAsXML());
            } else if (usecase.equals("getuser")) {
                sb.append(getUserAsXML(getEnvironment().getRequest().getParameter("id")));
            } else {
                sb.append("<no-such-yanel-usecase-implemented>" + usecase + "</no-such-yanel-usecase-implemented>");
            }
        } else {
            sb.append("<no-yanel-usecase/>");
        }
        sb.append("</security-api>");

        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    /**
     *
     */
    public boolean exists() {
        log.warn("TODO: Implementation not finished yet!");
        return true;
    }

    /**
     * Get all users
     */
    private StringBuilder getUsersAsXML() {
        StringBuilder sb = new StringBuilder("<users>");
        sb.append("</users>");
        return sb;
    }

    /**
     * Get a specific user
     * @param id User ID
     */
    private StringBuilder getUserAsXML(String id) {
        StringBuilder sb = new StringBuilder("<user id=\"" + id + "\">");
        sb.append("</user>");
        return sb;
    }
}
