/*
 * Copyright 2010 Wyona
 */

package org.wyona.yanel.impl.resources.securityapi;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import org.wyona.security.core.api.AccessManagementException;
import org.wyona.security.core.api.Group;
import org.wyona.security.core.api.GroupManager;
import org.wyona.security.core.api.Item;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

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
        try {
        String usecase = getEnvironment().getRequest().getParameter("yanel.usecase");
        if (usecase != null) {
            log.warn("DEBUG: Yanel usecase: " + usecase);
            sb.append("<yanel-usecase>" + usecase + "</yanel-usecase>");
            if (usecase.equals("getusers")) {
                sb.append(getUsersAsXML());
            } else if (usecase.equals("getuser")) {
                sb.append(getUserAsXML(getEnvironment().getRequest().getParameter("id")));
            } else if (usecase.equals("getgroups")) {
                sb.append(getGroupsAsXML());
            } else {
                sb.append("<no-such-yanel-usecase-implemented>" + usecase + "</no-such-yanel-usecase-implemented>");
            }
        } else {
            sb.append("<no-yanel-usecase/>");
        }
        } catch(Exception e) {
            log.error(e, e);
            sb.append("<exception>" + e.getMessage() + "</exception>");
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

    /**
     * Get all groups
     */
    private StringBuilder getGroupsAsXML() throws Exception {
        GroupManager gm = getRealm().getIdentityManager().getGroupManager();
        Group[] groups = gm.getGroups();
        Arrays.sort(groups, new ItemIDComparator());
        StringBuilder sb = new StringBuilder("<groups>");
        for (int i = 0; i < groups.length; i++) {
        sb.append("<group id=\"" + groups[i].getID() + "\">" + groups[i].getName() + "</group>");
        }
        sb.append("</groups>");
        return sb;
    }

    /**
     *
     */
    public class ItemIDComparator implements Comparator<Item> {
        public int compare(Item item1, Item item2) {
            try {
                String id1 = item1.getID();
                String id2 = item2.getID();
                return id1.compareToIgnoreCase(id2);
            } catch (AccessManagementException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}
