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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

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
            } else if (usecase.equals("deleteuser")) {
                log.warn("DEBUG: Delete user: " + getEnvironment().getRequest().getParameter("id"));
                deleteUser(getEnvironment().getRequest().getParameter("id"));
            } else if (usecase.equals("deletegroup")) {
                log.warn("DEBUG: Delete group: " + getEnvironment().getRequest().getParameter("id"));
                deleteGroup(getEnvironment().getRequest().getParameter("id"));
            } else if (usecase.equals("creategroup")) {
                log.warn("DEBUG: Create group: " + getEnvironment().getRequest().getParameter("id"));
                createGroup(getEnvironment().getRequest().getParameter("id"), getEnvironment().getRequest().getParameter("name"));
            } else if (usecase.equals("importuser")) {
                log.debug("Import user: " + getEnvironment().getRequest().getParameter("id"));
                importUser(getEnvironment().getRequest().getParameter("id"));
            } else if (usecase.equals("synchronize-users")) {
                if (getEnvironment().getRequest().getParameter("get-last-date") != null) {
                    if (getRealm().getRepository().getRootNode().hasNode("synchronization.properties")) {
                        sb.append("<last-successful-synchronization date=\"" + getRealm().getRepository().getNode("/synchronization.properties").getProperty("last-successful-synchronization").getDate() + "\"/>");
                    } else {
                        log.warn("Not synchronized yet!");
                        sb.append("<last-successful-synchronization date=\"" + "NOT_SYNCHRONIZED_YET" + "\"/>");
                    }
                } else {
                    // TODO: Lock ...
                    synchronizeUsers();
                    // TODO: Unlock ...

                    org.wyona.yarep.core.Node node;
                    if (!getRealm().getRepository().getRootNode().hasNode("synchronization.properties")) {
                        node = getRealm().getRepository().getRootNode().addNode("synchronization.properties", org.wyona.yarep.core.NodeType.RESOURCE);
                    } else {
                        node = getRealm().getRepository().getRootNode().getNode("synchronization.properties");
                    }
                    node.setProperty("last-successful-synchronization", new java.util.Date());
                }
            } else if (usecase.equals("getgroups")) {
                sb.append(getGroupsAsXML());
            } else if (usecase.equals("add-members-to-group")) {
                addMembersToGroup(getEnvironment().getRequest().getParameter("id"));
            } else if (usecase.equals("remove-members-from-group")) {
                removeMembersFromGroup(getEnvironment().getRequest().getParameter("id"));
            } else if (usecase.equals("getgroup")) {
                sb.append(getGroupAsXML(getEnvironment().getRequest().getParameter("id")));
            } else if (usecase.equals("deletepolicy")) {
                String path = getParameterAsString("path");
                String recursivelyText = getParameterAsString("deep");
                boolean recursively = "1".equals(recursivelyText);
                deletePolicy(path, recursively);
            } else {
                log.warn("No such usecase implemented: " + usecase);
                sb.append("<no-such-yanel-usecase-implemented>" + usecase + "</no-such-yanel-usecase-implemented>");
            }
        } else {
            log.warn("No usecase specified!");
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
    private StringBuilder getUserAsXML(String id) throws Exception {
        UserManager um = getRealm().getIdentityManager().getUserManager();
        User user = um.getUser(id);
        StringBuilder sb = new StringBuilder("<user id=\"" + id + "\">");
        sb.append("<name>" + user.getName() + "</name>");
        Group[] groups = user.getGroups();
        if (groups != null && groups.length > 0 ) {
            sb.append("<groups>");
            for (int i = 0; i < groups.length; i++) {
                sb.append("<group id=\"" + groups[i].getID() + "\"/>");
            }
            sb.append("</groups>");
        } else {
            sb.append("<!-- User is not member of any groups -->");
            log.info("User '" + id + "' is not member of any groups.");
        }
        sb.append("</user>");
        return sb;
    }

    /**
     * Get a specific group
     * @param id Group ID
     */
    private StringBuilder getGroupAsXML(String id) throws Exception {
        GroupManager gm = getRealm().getIdentityManager().getGroupManager();
        Group group = gm.getGroup(id);
        StringBuilder sb = new StringBuilder("<group xmlns=\"http://www.wyona.org/security/1.0\" id=\"" + id + "\">");

        String paraResolveGroups = getEnvironment().getRequest().getParameter("resolve-groups");
        boolean doResolveGroups = false;
        if (paraResolveGroups != null && paraResolveGroups.equals("true")) {
            log.warn("DEBUG: Resolve groups!");
            doResolveGroups = true;
        }

        java.util.List resolvedGroups = new java.util.ArrayList(); // INFO: Save info in order to detect loops
        resolvedGroups.add(group.getID());
        sb.append(getGroupMembers(gm, group, doResolveGroups, resolvedGroups));

        Group[] parentGroups = group.getParents();
        if (parentGroups != null && parentGroups.length > 0) {
            sb.append("<parents>");
            for (int i = 0; i < parentGroups.length; i++) {
                log.debug("Group: " + parentGroups[i].getID());
                sb.append("<group id=\"" + parentGroups[i].getID() + "\"/>");
            }
            sb.append("</parents>");
        } else {
            log.info("Group '" + group.getID() + "' has no parents!");
        }

        sb.append("</group>");
        return sb;
    }

    /**
     * Delete a specific user
     * @param id User ID
     */
    private void deleteUser(String id) throws AccessManagementException {
        UserManager um = getRealm().getIdentityManager().getUserManager();
        um.removeUser(id);
    }

    /**
     * Delete a specific group
     * @param id Group ID
     */
    private void deleteGroup(String id) throws AccessManagementException {
        GroupManager gm = getRealm().getIdentityManager().getGroupManager();
        gm.removeGroup(id);
    }

    /**
     * Create a group
     * @param id Group ID
     * @param name Group name
     */
    private void createGroup(String id, String name) throws AccessManagementException {
        GroupManager gm = getRealm().getIdentityManager().getGroupManager();
        gm.createGroup(id, name);
    }

    /**
     * Import a specific user, e.g. from LDAP, whereas this is a custom functionality and hence this method should be overwritten
     * @param id User ID
     */
    protected void importUser(String id) throws AccessManagementException {
        log.warn("Import user '" + id + "' NOT implemented! Please make sure to overwrite this method for your custom import.");
    }

    /**
     * Synchronize users, e.g. from LDAP, whereas this is a custom functionality and hence this method should be overwritten
     */
    protected void synchronizeUsers() throws AccessManagementException {
        log.warn("Synchronize users NOT implemented! Please make sure to overwrite this method for your custom import.");
    }

    /**
     * Add members (users and groups) to group
     * @param id Group ID to which new members will be added
     */
    private void addMembersToGroup(String id) throws AccessManagementException {
        GroupManager gm = getRealm().getIdentityManager().getGroupManager();
        UserManager um = getRealm().getIdentityManager().getUserManager();
        Group group = gm.getGroup(id);
        String[] members = getEnvironment().getRequest().getParameter("members").split(",");
        for (int i = 0; i < members.length; i++) {
            String typeID[] = members[i].split(":");
            if (typeID[0].equals("u")) {
                log.debug("Add user '" + typeID[1] + "' to group: " + id);
                group.addMember(um.getUser(typeID[1]));
            } else if (typeID[0].equals("g")) {
                log.debug("Add group '" + typeID[1] + "' to group: " + id);
                group.addMember(gm.getGroup(typeID[1]));
            }
        }
        group.save();
    }

    /**
     * Remove members (users and groups) from group
     * @param id Group ID from which members will be removed
     */
    private void removeMembersFromGroup(String id) throws AccessManagementException {
        GroupManager gm = getRealm().getIdentityManager().getGroupManager();
        UserManager um = getRealm().getIdentityManager().getUserManager();
        Group group = gm.getGroup(id);
        String[] members = getEnvironment().getRequest().getParameter("members").split(",");
        for (int i = 0; i < members.length; i++) {
            String typeID[] = members[i].split(":");
            if (typeID[0].equals("u")) {
                log.warn("DEBUG: Remove user '" + typeID[1] + "' from group: " + id);
                group.removeMember(um.getUser(typeID[1]));
            } else if (typeID[0].equals("g")) {
                log.warn("DEBUG: Remove group '" + typeID[1] + "' from group: " + id);
                group.removeMember(gm.getGroup(typeID[1]));
            }
        }
        group.save();
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

            if (users[i].getExpirationDate() != null) {
                //DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", java.util.Locale.ENGLISH); // INFO: IETF standard
                sb.append(" expiration-date=\"" + df.format(users[i].getExpirationDate()) + "\"");
            }

            if (users[i].getLanguage() != null) {
                sb.append(" language=\"" + users[i].getLanguage() + "\"");
            }

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

    /**
     * Get group members as XML
     */
    private String getGroupMembers(GroupManager gm, Group group, boolean doResolveGroups, java.util.List resolvedGroups) throws Exception {
        Item[] members = group.getMembers();
        StringBuilder sb = new StringBuilder();
        sb.append("<members>");
        // INFO: See policymanager/src/java/org/wyona/yanel/impl/resources/policymanager/PolicyManagerResource.java#resolveGroup(), also re Loops!
        for (int i = 0; i < members.length; i++) {
            //log.warn("DEBUG: Member: " + members[i].getID());
            if (members[i] instanceof User) {
                sb.append("<user id=\"" + members[i].getID() + "\"/>");
            } else if (members[i] instanceof Group) {
                if (doResolveGroups) {
                    if (alreadyResolved(members[i].getID(), resolvedGroups)) {
                        log.error("Loop detected: " + resolvedGroups);
                        sb.append("<loop-detected group-id=\"" + members[i].getID() + "\"/>");
                    } else {
                        sb.append("<group id=\"" + members[i].getID() + "\">");
                        //log.warn("DEBUG: Resolve group: " + members[i].getID());
                        resolvedGroups.add(members[i].getID());
                        sb.append(getGroupMembers(gm, gm.getGroup(members[i].getID()), doResolveGroups, resolvedGroups));
                        sb.append("</group>");
                    }
                } else {
                    sb.append("<group id=\"" + members[i].getID() + "\"/>");
                }
            } else {
                log.warn("No such instance of member/item implemented: " + members[i].getID());
            }
        }

        //sb.append("<user id=\"ep\" naz-blocked=\"true\"/>"); // TODO: naz ...
        //sb.append("<user id=\"fedpol_a\" naz-blocked=\"true\" naz-only-local=\"true\"/>");
        //sb.append("<user id=\"dz\" naz-only-local=\"true\"/>");

        sb.append("</members>");
        return sb.toString();
    }

    /**
     * Check whether group has been resolved already
     */
     private boolean alreadyResolved(String groupId, java.util.List resolvedGroupIDs) {
         for (int i = 0; i < resolvedGroupIDs.size(); i++) {
             if (groupId.equals((String)resolvedGroupIDs.get(i))) {
                 return true;
             }
         }
         return false;
     }
}
