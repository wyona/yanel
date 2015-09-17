/*
 * Copyright 2010 Wyona
 */
package org.wyona.yanel.impl.resources.yaneluser;

import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.impl.resources.BasicXMLResource;

import org.wyona.security.core.api.User;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.wyona.commons.xml.XMLHelper;

/**
 * A resource to edit/update the profile of a user
 */
public class EditYanelUserProfileResource extends BasicXMLResource {
    
    private static Logger log = LogManager.getLogger(EditYanelUserProfileResource.class);

    private String transformerParameterName;
    private String transformerParameterValue;

    private static final String USER_PROP_NAME = "user";
    
    /*
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        String oldPassword = getEnvironment().getRequest().getParameter("oldPassword");
        if (oldPassword != null) {
            String newPassword = getEnvironment().getRequest().getParameter("newPassword");
            String newPasswordConfirmed = getEnvironment().getRequest().getParameter("newPasswordConfirmation");
            updatePassword(oldPassword, newPassword, newPasswordConfirmed);
        }

        String email = getEnvironment().getRequest().getParameter("email");
        boolean emailUpdated = false;
        if (email != null) {
            emailUpdated = updateProfile(email);
            log.info("Email '" + email + "' has been updated: " + emailUpdated);
        }

        try {
            return getXMLAsStream(emailUpdated);
        } catch(Exception e) {
            log.error(e, e);
            return null;
        }
    }

    /**
     * Get user profile as XML as stream
     * @param emailUpdated Flag whether email got updated successfully
     */
    private java.io.InputStream getXMLAsStream(boolean emailUpdated) throws Exception {
        String userId = getUserId();
        if (userId != null) {
            Document doc = getUserProfile(userId, emailUpdated);
            return XMLHelper.getInputStream(doc, false, true, null);
        } else {
            return new java.io.StringBufferInputStream("<no-user-id/>");
        }
    }

    /**
     * Get user profile as DOM XML
     * @param userID ID of user
     * @param emailUpdated Flag whether email got updated successfully
     */
    protected Document getUserProfile(String userId, boolean emailUpdated) throws Exception {
        User user = realm.getIdentityManager().getUserManager().getUser(userId);

        Document doc = XMLHelper.createDocument(null, "user");
        Element rootEl = doc.getDocumentElement();
            rootEl.setAttribute("id", userId);
            rootEl.setAttribute("email", user.getEmail());
            rootEl.setAttribute("lamguage", user.getLanguage()); // DEPRECATED
            rootEl.setAttribute("language", user.getLanguage());
            if (emailUpdated) {
                rootEl.setAttribute("email-saved-successfully", "true");
            }

            Element nameEl = doc.createElement("name");
            nameEl.setTextContent(user.getName());
            rootEl.appendChild(nameEl);

            Element expirationDateEl = doc.createElement("expiration-date");
            expirationDateEl.setTextContent("" + user.getExpirationDate());
            rootEl.appendChild(expirationDateEl);

            Element descEl = doc.createElement("description");
            descEl.setTextContent("" + user.getDescription());
            rootEl.appendChild(descEl);

            org.wyona.security.core.api.Group[] groups = user.getGroups();
            if (groups !=  null && groups.length > 0) {
                Element groupsEl = doc.createElement("groups");
                rootEl.appendChild(groupsEl);
                for (int i = 0; i < groups.length; i++) {
                    Element groupEl = doc.createElement("group");
                    groupEl.setAttribute("id", groups[i].getID());
                    groupsEl.appendChild(groupEl);
                }
            }

            org.wyona.security.core.UserHistory history = user.getHistory();
            if (history != null) {
                java.util.List<org.wyona.security.core.UserHistory.HistoryEntry> entries = history.getHistory();
                for (org.wyona.security.core.UserHistory.HistoryEntry entry: entries) {
                    Element historyEl = (Element) rootEl.appendChild(doc.createElement("history"));
                    Element eventEl = (Element) historyEl.appendChild(doc.createElement("event"));
                    eventEl.setAttribute("usecase", entry.getUsecase());
                    eventEl.setAttribute("description", entry.getDescription());
                    eventEl.setAttribute("date", "" + entry.getDate());
                }
            }

            return doc;
    }

    /**
     * Get user ID, whereas check various options, such as 1) query string, 2) resource configuration, 3) URL and 4) session
     */
    protected String getUserId() throws Exception {
        String userId = null;

        // 1)
        userId = getEnvironment().getRequest().getParameter("id");
        if (userId != null) {
                return userId;
/*
            if (getRealm().getPolicyManager().authorize("/yanel/users/" + userId + ".html", getEnvironment().getIdentity(), new org.wyona.security.core.api.Usecase("view"))) { // INFO: Because the policymanager has no mean to check (or interpret) query strings we need to recheck programmatically
                return userId;
            } else {
                //throw new Exception("User '" + getEnvironment().getIdentity().getUsername() + "' tries to access user profile '" + userId + "', but is not authorized!");
                log.warn("User '" + getEnvironment().getIdentity().getUsername() + "' tries to access user profile '" + userId + "', but is not authorized!");
            }
*/
        } else {
            log.debug("User ID is not part of query string.");
        }

        // 2)
        userId = getResourceConfigProperty(USER_PROP_NAME);
        if (userId != null) {
            return userId;
        } else {
            log.debug("User ID is not configured inside resource configuration.");
        }

        // 3)
        userId = getPath().substring(getPath().lastIndexOf("/") + 1, getPath().lastIndexOf(".html"));
        if (userId != null && getRealm().getIdentityManager().getUserManager().existsUser(userId)) {
            return userId;
        } else {
            log.debug("Could not retrieve user ID from URL.");
        }

        // 4)
        userId = getEnvironment().getIdentity().getUsername();
        if (userId != null) {
            return userId;
        } else {
            log.warn("User does not seem to be signed in!");
        }

        throw new Exception("Cannot retrieve user ID!");
    }

    /**
     * Change user password
     * @param oldPassword Existing current password
     */
    protected void updatePassword(String oldPassword, String newPassword, String newPasswordConfirmed) throws Exception {
        String userId = getUserId();

        if (!getRealm().getIdentityManager().getUserManager().getUser(userId).authenticate(oldPassword)) {
            setTransformerParameter("error", "Authentication of user '" +userId + "' failed!");
            log.error("Authentication of user '" + userId + "' failed!");
            return;
        }

        if (newPassword != null && !newPassword.equals("")) {
            if (newPassword.equals(newPasswordConfirmed)) {
                User user = getRealm().getIdentityManager().getUserManager().getUser(userId);
                user.setPassword(newPassword);
                user.save();
                setTransformerParameter("success", "Password updated successfully");
            } else {
                setTransformerParameter("error", "New password and its confirmation do not match!");
            }
        } else {
            setTransformerParameter("error", "No new password was specified!");
        }
    }

    /**
     * Update the email address (and possibly also the alias) inside user profile
     * @param email New email address of user (and possibly also alias)
     * @return true if update was successful and false otherwise
     */
    protected boolean updateProfile(String email) throws Exception {
        if (email == null || ("").equals(email)) {
            setTransformerParameter("error", "emailNotSet");
            log.warn("No email (or empty email) specified, hence do not update email address!");
            return false;
        } else if (!validateEmail(email)) {
            setTransformerParameter("error", "emailNotValid");
            log.warn("Email '" + email + "' is not valid!");
            return false;
        } else {
            try {
                String userId = getUserId();
                org.wyona.security.core.api.UserManager userManager = realm.getIdentityManager().getUserManager();
                User user = userManager.getUser(userId);
                user.setName(getEnvironment().getRequest().getParameter("userName"));
                user.setLanguage(getEnvironment().getRequest().getParameter("user-profile-language"));

                String previousEmailAddress = user.getEmail();
                if (!previousEmailAddress.equals(email)) {
                    user.setEmail(email);

                    if (hasAlias(user, previousEmailAddress)) {
                        if (!hasAlias(user, email)) {
                            if (!userManager.existsAlias(email)) {
                                userManager.createAlias(email, userId);
                            } else {
                                throw new Exception("Alias '" + email + "' already exists!");
                            }
                        }
                        userManager.removeAlias(previousEmailAddress);
                        log.warn("Alias updated, which means user needs to use new email '" + email + "' to login.");
                    } else {
                        log.warn("Previous email '" + previousEmailAddress + "' was not used as alias, hence we also use new email '" + email + "' not as alias.");
                    }
                } else {
                    log.warn("DEBUG: Current email and new email are the same.");
                    if (!hasAlias(user, email)) {
                        log.warn("Email '" + email + "' is not used as alias yet!");
                    }
                }

                user.save();

                setTransformerParameter("success", "E-Mail (and alias) updated successfully");
                return true;
            } catch (Exception e) {
                log.error(e, e);
                setTransformerParameter("error", e.getMessage());
                return false;
            }
        }
    }

    /**
     * Check whether user has a specific alias
     * @return true when user has a specific alias
     */
    private boolean hasAlias(User user, String alias) throws Exception {
        String[] aliases = user.getAliases();
        for (int i = 0; i < aliases.length; i++) {
            if (aliases[i].equals(alias)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     */
    private void setTransformerParameter(String name, String value) {
        transformerParameterName = name;
        transformerParameterValue = value;
    }

    /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#passTransformerParameters(Transformer)
     */
    protected void passTransformerParameters(javax.xml.transform.Transformer transformer) throws Exception {
        super.passTransformerParameters(transformer);
        try {
            if (transformerParameterName != null && transformerParameterValue != null) {
                transformer.setParameter(transformerParameterName, transformerParameterValue);
                transformerParameterName = null;
                transformerParameterValue = null;
            }
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    /**
     * This method checks if the specified email is valid against a regex
     *
     * @param email
     * @return true if email is valid
     */
    private boolean validateEmail(String email) {
        String emailRegEx = "(\\w+)@(\\w+\\.)(\\w+)(\\.\\w+)*";
        Pattern pattern = Pattern.compile(emailRegEx);
        Matcher matcher = pattern.matcher(email);

        return matcher.find();
    }
}
