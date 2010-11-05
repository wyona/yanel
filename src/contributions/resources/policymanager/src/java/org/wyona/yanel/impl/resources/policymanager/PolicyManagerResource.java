/*
 * Copyright 2008-2010 Wyona
 */

package org.wyona.yanel.impl.resources.policymanager;

import org.wyona.security.core.GroupPolicy;
import org.wyona.security.core.IdentityPolicy;
import org.wyona.security.core.UsecasePolicy;
import org.wyona.security.core.api.AccessManagementException;
import org.wyona.security.core.api.Group;
import org.wyona.security.core.api.GroupManager;
import org.wyona.security.core.api.Identity;
import org.wyona.security.core.api.IdentityManager;
import org.wyona.security.core.api.Item;
import org.wyona.security.core.api.Policy;
import org.wyona.security.core.api.PolicyManager;
import org.wyona.security.core.api.User;
import org.wyona.security.core.api.UserManager;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


/**
 * Resource which acts as interface for editing tools in order to update/edit access policies
 */
public class PolicyManagerResource extends BasicXMLResource {
    
    private static final String REFRESH_USERS_RC_PROPERTY_NAME = "always-get-fresh-users";

    private static Logger log = Logger.getLogger(PolicyManagerResource.class);
    
    private static String PARAMETER_EDIT_PATH = "policy-path";
    private static String PARAMETER_USECASE = "yanel.policy";

    /**
     * See src/webapp/global-resource-configs/policy-manager_yanel-rc.xml or realm specific
     */
    @Override
    public View getView(String viewId) throws Exception {
        String policyRequestPara = getEnvironment().getRequest().getParameter(PARAMETER_USECASE);
        if (policyRequestPara == null) {
            policyRequestPara = "read";
        }
        if (policyRequestPara.equals("update")) {
            String getXML = getEnvironment().getRequest().getParameter("get");
            String postXML = getEnvironment().getRequest().getParameter("post");
            if (getXML != null) {
                viewId = "get-xml";
            } else if (postXML != null) {
                viewId = "post-xml";
            } else {
                viewId = "editor";
            }
        }
        
        return getXMLView(viewId, getContentXML(viewId));
    }

    /**
     *
     */
    @Override
    protected InputStream getContentXML(String viewId) throws Exception {

        // For example ?policy-path=/foo/bar.html
        String policyPath = request.getParameter(PARAMETER_EDIT_PATH);
        if (policyPath == null) {
            log.info("No policy path specified (e.g. ?policy-path=/foo/bar.html). Request path used as default: " + getPath());
            policyPath = getPath();
        }


        // For example ?yanel.policy=read
        String policyUsecase = "read";
        if (request.getParameter(PARAMETER_USECASE) != null) {
            policyUsecase = request.getParameter(PARAMETER_USECASE);
        } else {
            log.warn("No usecase set, hence use default usecase: " + policyUsecase);
        }
        
        String backToRealm = org.wyona.yanel.core.util.PathUtil.backToRealm(getPath());

        StringBuilder sb = new StringBuilder("");

            if (policyUsecase.equals("read")) {

                // Either order by usecases or identities
                String orderedByParam = request.getParameter("orderedBy");
                int orderedBy = 0;
                if (orderedByParam != null) orderedBy = new Integer(orderedByParam).intValue();
                // Either show parent policies or do not show them
                boolean showParents = false;
                String showParentsParam = request.getParameter("showParents");
                if (showParentsParam != null) showParents = new Boolean(showParentsParam).booleanValue();

                // Either show tabs or do not show them
                boolean showTabs = true;
                String showTabsParam = request.getParameter("showTabs");
                if (showTabsParam != null) showTabs = new Boolean(showTabsParam).booleanValue();

                boolean showAbbreviatedLabels = false;
                if (getResourceConfigProperty("show-abbreviated-labels") != null) {
                    showAbbreviatedLabels = Boolean.valueOf(getResourceConfigProperty("show-abbreviated-labels"));
                }

                if (viewId != null && viewId.equals("get-xml")) {
                    if (getEnvironment().getRequest().getParameter("aggregate") != null && getEnvironment().getRequest().getParameter("aggregate").equals("true")) {
                        log.warn("DEBUG: Get XML version of aggregated policy ...");
                        sb.append(getAggregatedPolicyAsXML(getPath(), null, orderedBy, showParents));
                    } else {
                        log.warn("DEBUG: Get XML version of policy ...");
                        sb.append(getPoliciesAsXML(getPath(), null, orderedBy, showParents));
                    }
                } else {
                    log.warn("DEBUG: Get XHTML version of policy ...");
                    sb.append(PolicyViewer.getXHTMLView(getRealm().getPolicyManager(), getRealm().getIdentityManager().getGroupManager(), getPath(), null, orderedBy, showParents, showTabs, showAbbreviatedLabels));
                }
            } else if (policyUsecase.equals("update")) {
                String getXML = request.getParameter("get");
                String postXML = request.getParameter("post");
                if (getXML != null && getXML.equals("identities")) {
                    //response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                    sb.append(getIdentitiesAndRightsAsXML(getRealm().getIdentityManager(), getRealm().getPolicyManager(), getRequestedLanguage()));
                } else if (getXML != null && getXML.equals("policy")) {
                    //response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                    sb.append(getPolicyAsXML(getRealm().getPolicyManager(), getPath()));
                } else if (postXML != null && postXML.equals("policy")) {
                    //response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                    try {
                        writePolicy(getEnvironment().getRequest().getInputStream(), getRealm().getPolicyManager(), getPath(), getRealm().getIdentityManager());
                        sb.append("<?xml version=\"1.0\"?><saved/>");
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        getEnvironment().getResponse().setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
                        sb.append("<?xml version=\"1.0\"?><not-saved>" + e.getMessage() + "</not-saved>");
                    }
                } else {
                    //response.setContentType("text/html; charset=" + DEFAULT_ENCODING);
                    String identitiesURL = backToRealm + getPath().substring(1) + "?" + PARAMETER_USECASE + "=update&amp;get=identities";
                    String policyURL = backToRealm + getPath().substring(1) + "?" + PARAMETER_USECASE + "=update&amp;get=policy";
                    String saveURL = backToRealm + getPath().substring(1) + "?" + PARAMETER_USECASE +"=update&amp;post=policy"; // This doesn't seem to work with all browsers!

                    String cancelURL = getReferer(backToRealm);
                    log.warn("DEBUG: Cancel URL: " + cancelURL);

                    // TODO: i18n
                    String title = "Edit Access Policy of Node '" + policyPath + "'";
                    if (getRequestedLanguage().equals("de")) {
                        title = "Bearbeiten der Zugriffsrechte des Node '" + policyPath + "'";
                    }

                    sb.append("<?xml version=\"1.0\"?>");
                    sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
                    sb.append("<head>");
                    sb.append("<title>" + title + "</title>");
                    sb.append("<meta name=\"generator\" content=\"" + this.getClass().getName() + "\"/>");

                    sb.append("<link rel=\"stylesheet\" href=\"" + PathUtil.getResourcesHtdocsPath(this) + "org.wyona.security.gwt.accesspolicyeditor.AccessPolicyEditor/style.css\" type=\"text/css\"/>");

                    // IMPORTANT: Please make sure that the value of 'cancel-url-base-equals-host-page-url' corresponds with getReferer()
                    // TODO: i18n-URL
                    sb.append("<script language=\"javascript\">var getURLs = {\"identities-url\": \"" + identitiesURL + "\", \"policy-url\": \"" + policyURL + "\", \"cancel-url\": \"" + cancelURL + "\", \"cancel-url-base-equals-host-page-url\": \"false\", \"save-url\": \"" + saveURL + "\", \"language\": \"" + getRequestedLanguage() + "\", \"i18n-url\": \"" + "TODO-i18n-URL" + "\"};</script><script language=\"javascript\" src=\"" +  PathUtil.getResourcesHtdocsPath(this) + "org.wyona.security.gwt.accesspolicyeditor.AccessPolicyEditor/org.wyona.security.gwt.accesspolicyeditor.AccessPolicyEditor.nocache.js\"></script>");

                    sb.append("</head>");
                    sb.append("<body><h1>" + title + "</h1><p><div id=\"access-policy-editor-hook\"></div></p></body></html>");
                }
            } else {
                //response.setContentType("text/html; charset=" + DEFAULT_ENCODING);
                getEnvironment().getResponse().setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
                sb.append("<html><body>Policy usecase not implemented yet: " + policyUsecase + "</body></html>");
                log.error("Policy usecase not implemented yet: " + policyUsecase);
            }
        return new ByteArrayInputStream(sb.toString().getBytes("utf-8"));
    }

    /**
     * Get users, groups and rights as XML
     */
    private String getIdentitiesAndRightsAsXML(IdentityManager im, PolicyManager pm, String language) {
        UserManager um = im.getUserManager();
        GroupManager gm = im.getGroupManager();

        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<access-control xmlns=\"http://www.wyona.org/security/1.0\">");

        try {
            boolean refreshUsers = true; // INFO: By default refresh all users, but please be aware that this can lead to severe performance issues, for example if used within a slow LDAP environment. Hence we make it configurable below.
            String refreshUsersText = getResourceConfigProperty(REFRESH_USERS_RC_PROPERTY_NAME);
            if (refreshUsersText != null && "false".equals(refreshUsersText)) {
                refreshUsers = false;
                log.warn("Users will not be loaded afresh!");
            }

            User[] users = um.getUsers(refreshUsers);
            Arrays.sort(users, new ItemIDComparator());
            appendSecurityItemsAsXML(users, "user", sb);

            Group[] groups = gm.getGroups();
            Arrays.sort(groups, new ItemIDComparator());
            appendSecurityItemsAsXML(groups, "group", sb);

            sb.append("<rights>");
            String[] rights = pm.getUsecases();
            if (rights != null) {
                for (int i = 0; i < rights.length; i++) {
                    sb.append("<right id=\"" + rights[i] + "\">" + rights[i] + " (" + pm.getUsecaseLabel(rights[i], language) + ")</right>");
                }
            }
            sb.append("</rights>");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sb.append("<exception>" + e.getMessage() + "</exception>");
        }
        sb.append("</access-control>");
        return sb.toString();
    }

    /**
     * Get XML for all users or groups
     * @param items Users or groups
     * @param itemXMLelementQName Element name, either user or group
     * @param a Appendable in order to write XML
     */
    private <I extends Item> void appendSecurityItemsAsXML(I[] items, String itemXMLelementQName, Appendable a) throws Exception {
        log.debug("Users or Groups ...");
        a.append("<"+itemXMLelementQName+"s ");
        a.append(">");
        for (int i = 0; i < items.length; i++) {
            I item = items[i];
            log.debug("User/Group: " + item.getName());
            appendSecurityItemAsXML(item, itemXMLelementQName, a);
        }
        a.append("</"+itemXMLelementQName+"s>");
    }

    /**
     * Get XML for one user or one group
     */
    private void appendSecurityItemAsXML(Item item, String itemXMLelementQName, Appendable a) throws Exception {
        a.append("<"+itemXMLelementQName+" id=\"" + item.getID() + "\"");
        a.append(">" + item.getName() + "</"+itemXMLelementQName+">");
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
     *
     */
    private String getPolicyAsXML(PolicyManager pm, String path) {

        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");

        try {
            Policy policy = pm.getPolicy(path, false);
            if (policy == null) {
                String useInheritedPolicies = "false"; // For backwards compatibility (and security) reasons we set it to false
                if (getResourceConfigProperty("use-inherited-policies-upon-creation") != null) {
                    useInheritedPolicies = getResourceConfigProperty("use-inherited-policies-upon-creation");
                }
                sb.append("<policy xmlns=\"http://www.wyona.org/security/1.0\" use-inherited-policies=\"" + useInheritedPolicies + "\">");
                log.warn("No policy yet for path: " + path + " (Return empty policy)");
            } else {
                sb.append("<policy xmlns=\"http://www.wyona.org/security/1.0\" use-inherited-policies=\"" + policy.useInheritedPolicies() + "\">");
                sb.append(getPolicyIdentities(policy));
                sb.append(getPolicyGroups(policy));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sb.append("<policy xmlns=\"http://www.wyona.org/security/1.0\">");
            sb.append("<exception>" + e.getMessage() + "</exception>");
        }

        sb.append("</policy>");
        return sb.toString();
    }

    /**
     * Get users (TODO: Move this code into the security package)
     * XXX(?) REFACTORME this method seems suspiciously similar to {@link #getPolicyGroups(Policy)}...
     */
    static public StringBuffer getPolicyIdentities(Policy p) {
        List<String> world = new LinkedList<String>();
        Map<String, List<String>> users = new HashMap<String, List<String>>();
        UsecasePolicy[] up = p.getUsecasePolicies();
        if (up != null && up.length > 0) {
            for (int i = 0; i < up.length; i++) {
                org.wyona.security.core.IdentityPolicy[] idps = up[i].getIdentityPolicies();
                for (int j = 0; j < idps.length; j++) {
                    //log.debug("Usecase Identity Policy: " + up[i].getName() + ", " + idps[j].getIdentity().getUsername() + ", " + idps[j].getPermission());

                    if (idps[j].getIdentity().isWorld()) {
                        world.add(up[i].getName());
                    } else {
                        List<String> userRights;
                        if ((userRights = users.get(idps[j].getIdentity().getUsername())) != null) {
                            log.debug("User has already been added: " + idps[j].getIdentity().getUsername());
                        } else {
                            userRights = new LinkedList<String>();
                            users.put(idps[j].getIdentity().getUsername(), userRights);
                        }
                        if (idps[j].getPermission()) {
                            userRights.add(up[i].getName());
                        }
                    }
                }
            }
        } else {
            log.warn("No policy usecases!");
        }

        StringBuffer sb = new StringBuffer();
        //sb.append("<li>WORLD (" + getCommaSeparatedList(world) + ")</li>");

        for (String userName : users.keySet()) {
            sb.append("<user id=\""+userName+"\">");
            List<String> rights = users.get(userName);
            for (String right : rights) {
                // TODO: Do not hardcode permission
                sb.append("<right id=\"" + right + "\" permission=\"true\"/>");
            }
            sb.append("</user>");
        }
        return sb;
    }

    /**
     * Get groups (TODO: Move this code into the security package)
     * XXX(?) REFACTORME this method seems suspiciously similar to {@link #getPolicyIdentities(Policy)}...
     */
    static public StringBuffer getPolicyGroups(Policy p) {
        Map<String, List<String>> groups = new HashMap<String, List<String>>();
        org.wyona.security.core.UsecasePolicy[] up = p.getUsecasePolicies();
        if (up != null && up.length > 0) {
            for (int i = 0; i < up.length; i++) {
                GroupPolicy[] ids = up[i].getGroupPolicies();
                for (int j = 0; j < ids.length; j++) {
                    List<String> groupRights;
                    if ((groupRights = groups.get(ids[j].getId())) != null) {
                        log.debug("Group has already been added: " + ids[j].getId());
                    } else {
                        groupRights = new LinkedList<String>();
                        groups.put(ids[j].getId(), groupRights);
                    }
                    if (ids[j].getPermission()) {
                        groupRights.add(up[i].getName());
                    }
                }
            }
        } else {
            log.warn("No policy usecases!");
        }

        StringBuffer sb = new StringBuffer();

        for (String userName : groups.keySet()) {
            sb.append("<group id=\""+userName+"\">");
            List<String> rights = groups.get(userName);
            for (String right : rights) {
                //TODO: Do not hardcode permission!
                sb.append("<right id=\"" + right + "\" permission=\"true\"/>");
            }
            sb.append("</group>");
        }
        return sb;
    }

    /**
     * Write/Save policy
     * @param policyAsInputStream Policy as XML
     * @param path Policy path
     */
    private void writePolicy(InputStream policyAsInputStream, PolicyManager pm, String path, IdentityManager im) throws Exception {
        Policy policy = new org.wyona.security.util.PolicyParser().parseXML(policyAsInputStream, im);

        // INFO: Add WORLD permissions, because policy editor does not support WORLD editing yet. As soon as the policy editor supports WORLD editing, then this piece of code becomes obsolete
        Policy originalPolicy = pm.getPolicy(path, false);
        if (originalPolicy != null) {
            org.wyona.security.core.UsecasePolicy[] up = originalPolicy.getUsecasePolicies();
            for (int i = 0; i < up.length; i++) {
                org.wyona.security.core.IdentityPolicy[] ip = up[i].getIdentityPolicies();
                for (int k = 0; k < ip.length; k++) {
                    if (ip[k].getIdentity().isWorld()) {
                        log.warn("Add WORLD to usecase: " + up[i].getName());
                        if (policy.getUsecasePolicy(up[i].getName()) != null) {
                            policy.getUsecasePolicy(up[i].getName()).addIdentity(new Identity(), ip[k].getPermission());
                        } else {
                            UsecasePolicy newUP = new org.wyona.security.core.UsecasePolicy(up[i].getName());
                            newUP.addIdentity(new Identity(), ip[k].getPermission());
                            policy.addUsecasePolicy(newUP);
                        }
                    }
                }
            }
        }

        pm.setPolicy(path, policy);
    }

    /**
     * Get referer (also see org.wyona.yanel.impl.resources.ResourceCreatorResource#getReferer())
     */
    private String getReferer(String backToRealm) throws Exception {
        // IMPORTANT: Please make sure that the below corresponds with 'cancel-url-base-equals-host-page-url'
        String referer = getEnvironment().getRequest().getHeader("Referer");
        if(referer != null) {
            java.net.URL url = new java.net.URL(referer);
            String filenameQSWithoutLeadingSlash = url.getFile().substring(url.getFile().lastIndexOf("/") + 1);
            log.warn("DEBUG: Manipulated referer: '" + filenameQSWithoutLeadingSlash + "'");
            // IMPORTANT: The below might cause problems with certain reverse proxys, whereas with httpd and mod_proxy it seems to be fine. If this should be a problem, then either strip off the webapp and realm prefix, or use the host-page-url as base and submit a relative path using the filenameQSWithoutLeadingSlash
            return replaceEntities(referer);
            //return backToRealm  + replaceEntities(url.getFile() + "?" + url.getQuery());
        } else {
            log.warn("No referer found!");
        }
        return getPath().substring(1); // Absolute
        //return backToRealm + getPath().substring(1); // Relative
    }

    /**
     * Replaces some characters by their corresponding xml entities.
     * This method escapes those characters which must not occur in an xml text node.
     * @param string
     * @return escaped string
     */
    private String replaceEntities(String str) {
        // there may be some &amp; and some & mixed in the input, so first transform all
        // &amp; to & and then transform all & back to &amp;
        // this way we don't get double escaped &amp;amp;
        str = str.replaceAll("&amp;", "&");
        str = str.replaceAll("&", "&amp;");
        str = str.replaceAll("<", "&lt;");
        return str;
    }

    /**
     * Get policies as XML
     *
     * @param path Content path which is associated with an access policy
     * @param contentItemId Content Item ID which allows a unique association with an access policy and an item within the content
     * @param orderedBy Allows ordering by usecases or identities
     * @param showParents Show the policies of the parent nodes, which allows to figure out how the policy has been aggregated
     */
    private StringBuilder getPoliciesAsXML(String path, String contentItemId, int orderedBy, boolean showParents) throws Exception {
        log.warn("DEBUG: Get policies for path: " + path);
        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\"?><policy-viewer xmlns=\"http://www.wyona.org/security/1.0\">");

        // TODO: ...
        sb.append("<usecases><usecase id=\"r\">Read</usecase><usecase id=\"w\">Write</usecase></usecases>");

        sb.append("<policies>");

        String[] names = path.split("/");
        StringBuilder currentPath = new StringBuilder();
        for (int i = 0; i < names.length; i++) {
            currentPath.append(names[i] + "/");
            boolean aggregate = false;
            Policy p = getRealm().getPolicyManager().getPolicy(currentPath.toString(), aggregate);

            if (p != null) {
                if (i == 0) {
                    sb.append("<node local-name=\"" + "/" + "\">");
                } else {
                    sb.append("<node local-name=\"" + names[i] + "\">");
                }
                sb.append("<policy use-inherited-policies=\"" + p.useInheritedPolicies() + "\">");
                UsecasePolicy[] up = p.getUsecasePolicies();
                for (int k = 0; k < up.length; k++) {
                    sb.append("<usecase id=\"" + up[k].getName() + "\">");
                    // TODO: Use ItemPolicy and cast check in order to get the right order
                    IdentityPolicy[] ip = up[k].getIdentityPolicies();
                    for (int j = 0; j < ip.length; j++) {
                        sb.append("<user id=\"" + ip[j].getId() + "\" permission=\"" + ip[j].getPermission() + "\" naz-blocked=\"true\" naz-permission-unlike-group=\"true\"/>"); // TODO: naz ...
                    }
                    GroupPolicy[] gp = up[k].getGroupPolicies();
                    for (int j = 0; j < gp.length; j++) {
                        sb.append("<group id=\"" + gp[j].getId() + "\" permission=\"" + gp[j].getPermission() + "\" naz-permission-unlike-members=\"true\"/>"); // TODO: naz ...
                    }
                    sb.append("</usecase>");
                }
                sb.append("</policy>");
                sb.append("</node>");
            } else {
                sb.append("<node local-name=\"" + names[i] + "\"/>");
            }
        }

        sb.append("</policies>");
        sb.append("</policy-viewer>");
        return sb;
    }

    /**
     * Get aggregated policy as XML
     *
     * @param path Content path which is associated with an access policy
     * @param contentItemId Content Item ID which allows a unique association with an access policy and an item within the content
     * @param orderedBy Allows ordering by usecases or identities
     * @param showParents Show the policies of the parent nodes, which allows to figure out how the policy has been aggregated
     */
    protected StringBuilder getAggregatedPolicyAsXML(String path, String contentItemId, int orderedBy, boolean showParents) throws Exception {
        log.warn("DEBUG: Get aggregated policy for path: " + path);
        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\"?><policy-viewer xmlns=\"http://www.wyona.org/security/1.0\">");

        // TODO: ...
        sb.append("<usecases><usecase id=\"r\">Read</usecase><usecase id=\"w\">Write</usecase></usecases>");

        sb.append("<policies>");

            boolean aggregate = true;
            Policy p = getRealm().getPolicyManager().getPolicy(path, aggregate);

            if (p != null) {
                sb.append("<node name=\"" + path + "\">");
                sb.append("<policy use-inherited-policies=\"" + p.useInheritedPolicies() + "\">");
                UsecasePolicy[] up = p.getUsecasePolicies();
                for (int k = 0; k < up.length; k++) {
                    // TODO: Do not list 'rw' users within 'r-' users! QUESTION: How to make this generic?!
                    sb.append("<usecase id=\"" + up[k].getName() + "\">");

                    // TODO (optional): Use ItemPolicy and cast check in order to get the right order!
                    List mergedListOfUserPolicies = new java.util.ArrayList();

                    IdentityPolicy[] ip = up[k].getIdentityPolicies();
                    for (int j = 0; j < ip.length; j++) {
                        mergedListOfUserPolicies.add(ip[j]);
                    }

                    GroupPolicy[] gp = up[k].getGroupPolicies();
                    for (int j = 0; j < gp.length; j++) {
                        List resolvedGroups = new java.util.ArrayList();
                        resolvedGroups.add(gp[j].getId());
                        User[] groupUsers = resolveGroup(gp[j].getId(), resolvedGroups);
                        for (int i = 0; i < groupUsers.length; i++) {
                            if (!existsWithinMergedList(groupUsers[i].getID(), mergedListOfUserPolicies)) {
                                String id = groupUsers[i].getID();
                                mergedListOfUserPolicies.add(new IdentityPolicy(new Identity(id, id), gp[j].getPermission()));
                            }
                        }
                    }

                    for (int j = 0; j < mergedListOfUserPolicies.size(); j++) {
                        IdentityPolicy identityPolicy = (IdentityPolicy) mergedListOfUserPolicies.get(j);
                        sb.append("<user id=\"" + identityPolicy.getId() + "\" permission=\"" + identityPolicy.getPermission() + "\"/>");
                    }
                    sb.append("</usecase>");
                }
                sb.append("</policy>");
                sb.append("</node>");
            } else {
                sb.append("<node name=\"" + path + "\"/>");
            }

        sb.append("</policies>");
        sb.append("</policy-viewer>");
        return sb;
    }

    /**
     * Get all users of a particular group and its sub-groups
     * @param groupdId Group ID
     * @param resolvedGroupIDs Resolved group IDs in order to detect loops
     */
    protected User[] resolveGroup(String groupId, List resolvedGroupIDs) throws Exception {
        Item[] members = getRealm().getIdentityManager().getGroupManager().getGroup(groupId).getMembers();
        List users = new java.util.ArrayList();
        for (int i = 0; i < members.length; i++) {
            if (members[i] instanceof User) {
                users.add((User)members[i]);
            } else if (members[i] instanceof Group) {
                if (alreadyResolved(members[i].getID(), resolvedGroupIDs)) {
                    log.warn("Loop detected for group '" + groupId + "' and sub-group '" + members[i].getID() + "'!");
                } else {
                    log.debug("Sub-group '" + members[i].getID() + "' of group '" + groupId + "' needs to be resolved!");
                    resolvedGroupIDs.add(members[i].getID());
                    User[] subGroupUsers = resolveGroup(members[i].getID(), resolvedGroupIDs);
                    for (int j = 0;j < subGroupUsers.length; j++) {
                        // TODO: Check if user already exists within parent group
                        users.add(subGroupUsers[j]);
                    }
                }
            } else {
                log.warn("No such member type implemented (Member: '" + members[i].getID() + "', Group: '" + groupId + "')!");
            }
        }
        return (User[])users.toArray(new User[users.size()]);
    }

    /**
     * Check whether group has been resolved already
     */
     private boolean alreadyResolved(String groupId, List resolvedGroupIDs) {
         for (int i = 0; i < resolvedGroupIDs.size(); i++) {
             if (groupId.equals((String)resolvedGroupIDs.get(i))) {
                 return true;
             }
         }
         return false;
     }

    /**
     * Check whether user is part of merged list
     */
     protected boolean existsWithinMergedList(String userId, List mergedListOfUserPolicies) {
         for (int i = 0; i < mergedListOfUserPolicies.size(); i++) {
             if (userId.equals(((IdentityPolicy)mergedListOfUserPolicies.get(i)).getId())) {
                 return true;
             }
         }
         return false;
     }
}
