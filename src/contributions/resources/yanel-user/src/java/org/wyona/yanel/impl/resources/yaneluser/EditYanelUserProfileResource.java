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

import org.apache.log4j.Logger;

/**
 * A resource to edit/update the profile of a user
 */
public class EditYanelUserProfileResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(EditYanelUserProfileResource.class);

    private String transformerParameterName;
    private String transformerParameterValue;
    
    /*
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    protected InputStream getContentXML(String viewId) {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        String oldPassword = getEnvironment().getRequest().getParameter("oldPassword");
        if (oldPassword != null) {
            updatePassword(oldPassword);
        }

        String email = getEnvironment().getRequest().getParameter("email");
        if (email != null) {
            updateProfile(email);
        }

        try {
            return getXMLAsStream();
        } catch(Exception e) {
            log.error(e, e);
            return null;
        }
    }

    /**
     * Get user profile as XML as stream
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

        // 1)
        if (getEnvironment().getRequest().getParameter("id") != null) {
            return getEnvironment().getRequest().getParameter("id");
        }

        // 2)
        ResourceConfiguration resConfig = getConfiguration();
        String userId = null;
        if(resConfig != null) {
            userId = getConfiguration().getProperty("user");
        } else {
            log.warn("DEPRECATED: Do not use RTI but rather a resource configuration");
            userId = getRTI().getProperty("user");
        }
        if (userId != null) {
            return userId;
        }

        // 3)
        final String userName = getPath().substring(getPath().lastIndexOf("/") + 1, getPath().lastIndexOf(".html"));
        log.debug("User name: " + userName);
        if (userName != null && getRealm().getIdentityManager().getUserManager().existsUser(userName)) {
            return userName;
        } else {
            throw new Exception("No such user '" + userName + "'");
        }
    }

    /**
     * Change user password
     *
     * @param oldPassword Existing current password
     */
    private void updatePassword(String oldPassword) {
        try {
            String userId = getUserId();

            if (!getRealm().getIdentityManager().getUserManager().getUser(userId).authenticate(oldPassword)) {
                setTransformerParameter("error", "Authentication of user '" +userId + "' failed!");
                log.error("Authentication of user '" + userId + "' failed!");
                return;
            }

            String newPassword = getEnvironment().getRequest().getParameter("newPassword");
            String newPasswordConfirmed = getEnvironment().getRequest().getParameter("newPasswordConfirmation");
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
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    /**
     * Updates the user profile
     *
     * @param email E-Mail address of user
     */
    private void updateProfile(String email) {
        if (email == null || ("").equals(email)) {
            setTransformerParameter("error", "emailNotSet");
        } else if (!validateEmail(email)) {
            setTransformerParameter("error", "emailNotValid");
        } else {
            try {
                String userId = getUserId();
                User user = realm.getIdentityManager().getUserManager().getUser(userId);
                user.setEmail(getEnvironment().getRequest().getParameter("email"));
                user.setName(getEnvironment().getRequest().getParameter("userName"));
                user.setLanguage(getEnvironment().getRequest().getParameter("user-profile-language"));
                user.save();
                setTransformerParameter("success", "Profile updated successfully");
            } catch (Exception e) {
                log.error(e, e);
                setTransformerParameter("error", e.getMessage());
            }
        }
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
