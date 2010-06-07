/*
 * Copyright 2010 Wyona
 */
package org.wyona.yanel.impl.resources.yaneluser;

import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.impl.resources.BasicXMLResource;

import org.wyona.security.core.api.User;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;


/**
 * A resource to edit/update the profile of a user
 */
public class EditYanelUserProfileResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(EditYanelUserProfileResource.class);
    
    /*
     * This method overrides the method to create the InputStream called by BasicXMLResource
     * Since you extend the BasicXMLResource this has to contain well-formed xml.
     * Should return a InputStream which contains XML. 
     * Use String, StingBuffer, dom, jdom, org.apache.commons.io.IOUtils and so on to generate the XML.
     */
    protected InputStream getContentXML(String viewId) {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }
        try {
            return getXMLAsStream();
        } catch(Exception e) {
            log.error(e, e);
            return null;
        }
    }

    /**
     * Get XML as stream
     */
    private java.io.InputStream getXMLAsStream() throws Exception {
        String userId = getUserId();
        if (userId != null) {
            User user = realm.getIdentityManager().getUserManager().getUser(userId);
            StringBuilder sb = new StringBuilder();

            sb.append("<?xml version=\"1.0\"?>");
            sb.append("<user id=\"" + userId + "\" email=\"" + user.getEmail() + "\" language=\"" + user.getLanguage() + "\">");
            sb.append("  <name>" + user.getName() + "</name>");
            sb.append("  <expiration-date>" + user.getExpirationDate() + "</expiration-date>");
            sb.append("  <description>" + user.getDescription() + "</description>");

            org.wyona.security.core.api.Group[] groups = user.getGroups();
            if (groups !=  null && groups.length > 0) {
                sb.append("  <groups>");
                for (int i = 0; i < groups.length; i++) {
                    sb.append("  <group id=\"" + groups[i].getID() + "\"/>");
                }
                sb.append("  </groups>");
            }
            sb.append("</user>");

            return new java.io.StringBufferInputStream(sb.toString());
        } else {
            return new java.io.StringBufferInputStream("<no-user-id/>");
        }
    }

    /**
     * Get user id from resource configuration
     */
    private String getUserId() throws Exception {
        String userId = null;

        if (getEnvironment().getRequest().getParameter("id") != null) {
            return getEnvironment().getRequest().getParameter("id");
        }
        ResourceConfiguration resConfig = getConfiguration();
        if(resConfig != null) {
            userId = getConfiguration().getProperty("user");
        } else {
            log.warn("DEPRECATED: Do not use RTI but rather a resource configuration");
            userId = getRTI().getProperty("user");
        }
        return userId;
    }
}
