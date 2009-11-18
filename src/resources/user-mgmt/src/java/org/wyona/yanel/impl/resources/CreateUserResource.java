/*
 * Copyright 2007-2009 Wyona
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

import org.apache.log4j.Logger;

import org.wyona.security.core.api.AccessManagementException;
import org.wyona.security.core.api.UserManager;
import org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource;
import org.wyona.yanel.impl.resources.usecase.UsecaseException;

import java.util.regex.Pattern;

/**
 * Create new user
 */
public class CreateUserResource extends ExecutableUsecaseResource {

    private static final Logger log = Logger.getLogger(CreateUserResource.class);

    private static final String PARAM_USER_ID = "userID";
    private static final String PARAM_NAME = "name";
    private static final String PARAM_EMAIL = "email";
    private static final String PARAM_PASSWORD1 = "password1";
    private static final String PARAM_PASSWORD2 = "password2";

    @Override
    public void execute() throws UsecaseException {
        UserManager userManager = getRealm().getIdentityManager().getUserManager();
        String id = getParameterAsString(PARAM_USER_ID);
        String name = getParameterAsString(PARAM_NAME);
        String email = getParameterAsString(PARAM_EMAIL);
        String password = getParameterAsString(PARAM_PASSWORD1);
        try {
            if (log.isDebugEnabled()) {
                log.debug("creating user: " + id + " " + name + " " + email);
            }
            userManager.createUser(id, name, email, password);
            addInfoMessage("User '" + id + "' (" + name + ") created successfully. (IMPORTANT: Please make sure to add user either to an existing group or to an access policy, because otherwise user will not have any explicite rights.)");
        } catch (AccessManagementException e) {
            throw new UsecaseException(e.getMessage(), e);
        }
    }

    @Override
    public boolean checkPreconditions() throws UsecaseException {
        String id = getParameterAsString(PARAM_USER_ID);
        String name = getParameterAsString(PARAM_NAME);
        String email = getParameterAsString(PARAM_EMAIL);
        String password1 = getParameterAsString(PARAM_PASSWORD1);
        String password2 = getParameterAsString(PARAM_PASSWORD2);
        
        if (id == null || id.length() == 0) {
            this.addError("Please enter a user ID.");
            return false;
        } else {
            Pattern pattern = Pattern.compile("[a-z0-9[-][_][@]]*");
            if (!pattern.matcher(id).matches()) {
                this.addError("Please enter a user ID containing only characters: 'a-z' or '0-9' or '-' or '_'!");
                return false;
            }
        }
        UserManager userManager = getRealm().getIdentityManager().getUserManager();
        try {
            if (userManager.existsUser(id)) {
                this.addError("User with this ID exists already.");
                return false;
            }
        } catch (AccessManagementException e) {
            throw new UsecaseException(e.getMessage(), e);
        }

        if (name == null || name.length() == 0) {
            this.addError("Please enter a user name.");
            return false;
        }

        if (email == null || email.length() == 0) {
            this.addError("Please enter an email address.");
            return false;
        } else if(email.indexOf("@") <= 0) { // TODO: Implement some more strict validation
            this.addError("Please enter a valid email address!");
            return false;
        }

        if (password1 == null || password1.length() < 6) { // TODO: Implement some more strict validation
            this.addError("Please enter a password with at least 6 characters.");
            return false;
        }

        if (!password1.equals(password2)) {
            this.addError("Passwords don't match.");
            return false;
        }

        return true;
    }
}
