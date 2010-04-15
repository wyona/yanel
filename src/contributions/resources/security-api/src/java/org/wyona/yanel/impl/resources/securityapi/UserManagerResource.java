/*
 * Copyright 2010 Wyona
 */

package org.wyona.yanel.impl.resources.securityapi;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import org.wyona.security.core.api.AccessManagementException;
import org.wyona.security.core.api.Group;
import org.wyona.security.core.api.GroupManager;
import org.wyona.security.core.api.Item;
import org.wyona.security.core.api.PolicyManager;
import org.wyona.security.core.api.User;
import org.wyona.security.core.api.UserManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Collections;

import org.apache.log4j.Logger;

/**
 * Read and write XML re UserManager
 */
public class UserManagerResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(UserManagerResource.class);
    
    @Override
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
            } else if (usecase.equals("deletepolicy")) {
                String path = getParameterAsString("path");
                String recursivelyText = getParameterAsString("deep");
                boolean recursively = "1".equals(recursivelyText);
                deletePolicy(path, recursively);
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

    @Override
    public boolean exists() {
        log.warn("TODO: Implementation not finished yet!");
        return true;
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

        StringBuilder sb = new StringBuilder();
        sb.append("<groups");

        // INFO: Add custom namespaces
        Map<String, String> extraXMLnamespaceDeclarations = getExtraXMLnamespaceDeclarations();
        for (Map.Entry<String, String> declaration : extraXMLnamespaceDeclarations.entrySet()) {
            sb.append(" xmlns:" + declaration.getKey() + "=\"" + declaration.getValue() + "\"");
        }

        sb.append(">");

        for (int i = 0; i < groups.length; i++) {
            sb.append("<group id=\"" + groups[i].getID() + "\"");

            // INFO: Add custom properties
            SecurityItemExtraPropertiesGetter<Group> itemExtraPropertiesGetter = getGroupExtraPropertiesGetter();
            Map<String, String> extraItemProperties = itemExtraPropertiesGetter.getExtraProperties(groups[i]);
            for (Map.Entry<String, String> property : extraItemProperties.entrySet()) {
                sb.append(" " + property.getKey() + "=\"" + org.wyona.commons.xml.XMLHelper.replaceEntities(property.getValue()) + "\""); //INFO: The name should be safe, so don't escape it
            }

            sb.append(">" + groups[i].getName() + "</group>");
        }
        sb.append("</groups>");
        return sb;
    }

    /**
     * Deletes a specific policy.
     * @param id the policy ID
     */
    private void deletePolicy(String path, boolean recursively) throws Exception {
        PolicyManager pm = getRealm().getPolicyManager();
        if (recursively) {
            log.warn("Recursively deletion of policies not yet implemented, only policy "+path+" will be deleted.");
        }
        pm.removePolicy(path);
    }

    /**
     * Get all users
     */
    private StringBuilder getUsersAsXML() throws Exception {
        UserManager um = getRealm().getIdentityManager().getUserManager();

        boolean refresh = true;
        if (getResourceConfigProperty("refresh-users") != null) {
            refresh = new Boolean(getResourceConfigProperty("refresh-users")).booleanValue();
        } else {
            log.warn("No refresh user property set within resource configuration '" + getConfiguration().getNode() + "', hence will use true as default.");
        }

        User[] users = um.getUsers(refresh);
        Arrays.sort(users, new ItemIDComparator());

        StringBuilder sb = new StringBuilder();

        sb.append("<users");

        // INFO: Add custom namespaces
        Map<String, String> extraXMLnamespaceDeclarations = getExtraXMLnamespaceDeclarations();
        for (Map.Entry<String, String> declaration : extraXMLnamespaceDeclarations.entrySet()) {
            sb.append(" xmlns:" + declaration.getKey() + "=\"" + declaration.getValue() + "\"");
        }

        sb.append(">");

        for (int i = 0; i < users.length; i++) {
            sb.append("<user id=\"" + users[i].getID() + "\"");
            sb.append(" expired=\"" + org.wyona.security.impl.util.UserUtil.isExpired(users[i]) + "\"");
            sb.append(" expiration-date=\"" + users[i].getExpirationDate() + "\"");
            sb.append(" language=\"" + users[i].getLanguage() + "\"");

            // INFO: Add custom properties
            SecurityItemExtraPropertiesGetter<User> itemExtraPropertiesGetter = getUserExtraPropertiesGetter();
            Map<String, String> extraItemProperties = itemExtraPropertiesGetter.getExtraProperties(users[i]);
            for (Map.Entry<String, String> property : extraItemProperties.entrySet()) {
                sb.append(" " + property.getKey() + "=\"" + org.wyona.commons.xml.XMLHelper.replaceEntities(property.getValue()) + "\""); //INFO: The name should be safe, so don't escape it
            }

            sb.append(">" + users[i].getName() + "</user>");
        }
        sb.append("</users>");
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

    /**
     * Interface/template in order to get custom properties of user or group
     */
    public interface SecurityItemExtraPropertiesGetter<I extends Item> {

        /**
         * Get custom properties
         * @param item User, group, host, etc.
         */
        Map<String, String> getExtraProperties(I item);
    }

    /**
     * Default implementation of getter for user
     */
    protected SecurityItemExtraPropertiesGetter<User> getUserExtraPropertiesGetter() {
        return userNoExtraPropertiesGetter;
    }

    /**
     * Default user properties which will be used by default implementation #getUserExtraPropertiesGetter
     */
    private static final SecurityItemExtraPropertiesGetter<User> userNoExtraPropertiesGetter = new SecurityItemExtraPropertiesGetter<User>() {
        @Override
        public Map<String, String> getExtraProperties(User item) {
            return Collections.emptyMap();// no extra properties to add for standard Yanel users
        }
    };

    /**
     * Default implementation of getter for group
     */
    protected SecurityItemExtraPropertiesGetter<Group> getGroupExtraPropertiesGetter() {
        return groupNoExtraPropertiesGetter;
    }

    /**
     * Default group properties which will be used by default implementation #getGroupExtraPropertiesGetter
     */
    private static final SecurityItemExtraPropertiesGetter<Group> groupNoExtraPropertiesGetter = new SecurityItemExtraPropertiesGetter<Group>() {
        @Override
        public Map<String, String> getExtraProperties(Group item) {
            return Collections.emptyMap();// no extra properties to add for standard Yanel users
        }
    };

    /**
     * Overwrite this method in order to insert custom namespaces
     */
    protected Map<String, String> getExtraXMLnamespaceDeclarations() throws Exception {
        return Collections.emptyMap(); // DEFAULT: No extra XML namespace declarations
    }
}
