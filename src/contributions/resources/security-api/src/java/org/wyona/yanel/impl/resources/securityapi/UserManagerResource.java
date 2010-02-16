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
        String usecase = getEnvironment().getRequest().getParameter("yanel.usecase");
        if (usecase != null) {
            log.warn("DEBUG: Yanel usecase: " + usecase);
            if (usecase.equals("getusers")) {
                sb.append("<yanel-usecase>" + usecase + "</yanel-usecase>");
            } else {
                sb.append("<no-such-yanel-usecase>" + usecase + "</no-such-yanel-usecase>");
            }
        } else {
            sb.append("<no-yanel-usecase/>");
        }

        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    /**
     *
     */
    public boolean exists() {
        log.warn("TODO: Implementation not finished yet!");
        return true;
    }
}
