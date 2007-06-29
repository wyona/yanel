/*
 * Copyright 2006 Wyona
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.wyona.org/licenses/APACHE-LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wyona.yanel.impl.resources;

import org.apache.log4j.Category;
import org.wyona.security.core.api.AccessManagementException;
import org.wyona.security.core.api.Group;
import org.wyona.security.core.api.GroupManager;
import org.wyona.security.core.api.User;
import org.wyona.security.core.api.UserManager;
import org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource;
import org.wyona.yanel.impl.resources.usecase.UsecaseException;


/**
 * Update a user profile.
 * This usecase can be configured with two resource config properties:
 * - allowEditGroups: allow to edit the group affiliations of the user
 * - verifyPassword: the user has to enter the current password 
 * 
 * This usecase may be used in two different situations:
 * 1. a user wants to update his own profile
 * 2. an admin user wants to update the profile of another user
 */
public class UpdateUserResource extends ExecutableUsecaseResource {

    private static Category log = Category.getInstance(UpdateUserResource.class);
    
    private static final String PARAM_USER_ID = "userID";
    private static final String PARAM_NAME = "name";
    private static final String PARAM_EMAIL = "email";
    private static final String PARAM_OLD_PASSWORD = "oldPassword";
    private static final String PARAM_PASSWORD1 = "password1";
    private static final String PARAM_PASSWORD2 = "password2";
    private static final String PARAM_GROUPS = "groups";
    private static final String PARAM_USECASE_STATE = "usecaseState";
    
    private static final String PROPERTY_ALLOW_EDIT_GROUPS = "allowEditGroups";
    private static final String PROPERTY_VERIFY_PASSWORD = "verifyPassword";

    public void cancel() {
        // don't do anything
    }

    protected void init() throws UsecaseException {
        super.init();
        try {
            User user = getUser();
            if (getParameter(PARAM_USECASE_STATE) == null) {
                // set initial parameter values:
                setParameter(PARAM_NAME, user.getName());
                setParameter(PARAM_EMAIL, user.getEmail());
                Group[] groups = user.getGroups();
                String[] groupIDs = new String[groups.length];
                for (int i = 0; i < groups.length; i++) {
                    groupIDs[i] = groups[i].getID();
                }
                setParameter(PARAM_GROUPS, groupIDs);
                setParameter(PARAM_USECASE_STATE, "initialized");
            }
        } catch (AccessManagementException e) {
            throw new UsecaseException(e.getMessage(), e);
        }
    }
    
    public void execute() throws UsecaseException {
        String name = getParameterAsString(PARAM_NAME);
        String email = getParameterAsString(PARAM_EMAIL);
        String password = getParameterAsString(PARAM_PASSWORD1);
        String[] groupIDs = getParameterAsStringValues(PARAM_GROUPS);
        try {
            User user = getUser();
            if (log.isDebugEnabled()) {
                log.debug("updating user: " + user.getID() + " " + name + " " + email);
            }
            user.setName(name);
            user.setEmail(email);
            if (password != null && password.length() > 0) {
                user.setPassword(password);
            }
            String allowEditGroups = this.getResourceConfigProperty(PROPERTY_ALLOW_EDIT_GROUPS);
            if (allowEditGroups != null && allowEditGroups.equals("true")) {
                Group[] groups = getGroups();
                for (int i = 0; i < groups.length; i++) {
                    boolean matched = false;
                    for (int j = 0; j < groupIDs.length; j++) {
                        if (groups[i].getID().equals(groupIDs[j])) {
                            matched = true;
                            break;
                        }
                    }
                    if (matched && !groups[i].isMember(user)) {
                        if (log.isDebugEnabled()) {
                            log.debug("adding user '" + user.getID() + "' to group '" + groups[i].getID() + "'");
                        }
                        groups[i].addMember(user);
                        groups[i].save();
                    }
                    if (!matched && groups[i].isMember(user)) {
                        if (log.isDebugEnabled()) {
                            log.debug("removing user '" + user.getID() + "' from group '" + groups[i].getID() + "'");
                        }
                        groups[i].removeMember(user);
                        groups[i].save();
                    }
                }
            }
            user.save();
            addInfoMessage("User " + user.getID() + " (" + name + ") updated successfully.");
        } catch (AccessManagementException e) {
            log.error(e, e);
            throw new UsecaseException(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e, e);
            throw new UsecaseException(e.getMessage(), e);
        }
    }

    public boolean checkPreconditions() throws UsecaseException {
        String name = getParameterAsString(PARAM_NAME);
        String email = getParameterAsString(PARAM_EMAIL);
        String oldPassword = getParameterAsString(PARAM_OLD_PASSWORD);
        String password1 = getParameterAsString(PARAM_PASSWORD1);
        String password2 = getParameterAsString(PARAM_PASSWORD2);
        
        if (name == null || name.length()==0) {
            this.addError("Please enter a user name.");
            return false;
        }
        if (email == null || email.length()==0) {
            this.addError("Please enter an email address.");
            return false;
        }
        if (password1 != null && password1.length() > 0 &&  password1.length() < 6) {
            this.addError("Please enter a password with at least 6 characters.");
            return false;
        }
        if (!password1.equals(password2)) {
            this.addError("Passwords don't match.");
            return false;
        }
        try {
            String verifyPassword = this.getResourceConfigProperty(PROPERTY_VERIFY_PASSWORD);
            if (verifyPassword != null && verifyPassword.equals("true")) {
                User user = getUser();
                if (!user.authenticate(oldPassword)) {
                    this.addError("Current password is not correct.");
                    return false;
                }
            }
        } catch (Exception e) {
            throw new UsecaseException(e.getMessage(), e);
        }
        return true;
    }
    
    public User getUser() throws AccessManagementException {
        String id = getParameterAsString(PARAM_USER_ID);
        UserManager userManager = getRealm().getIdentityManager().getUserManager();
        User user = userManager.getUser(id);
        return user;
    }
    
    public Group[] getGroups() throws AccessManagementException {
        GroupManager groupManager = getRealm().getIdentityManager().getGroupManager();
        return groupManager.getGroups();
    }
    
    public boolean isGroupSelected(Group group) throws AccessManagementException {
        String[] groupIDs = (String[])getParameter(PARAM_GROUPS);
        
        for (int i = 0; i < groupIDs.length; i++) {
            if (groupIDs[i].equals(group.getID())) {
                return true;
            }
        }
        return false;
    }
}
