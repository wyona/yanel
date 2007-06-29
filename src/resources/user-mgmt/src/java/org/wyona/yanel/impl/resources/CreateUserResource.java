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
import org.wyona.security.core.api.UserManager;
import org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource;
import org.wyona.yanel.impl.resources.usecase.UsecaseException;


/**
 *
 */
public class CreateUserResource extends ExecutableUsecaseResource {

    private static Category log = Category.getInstance(CreateUserResource.class);

    private static final String PARAM_USER_ID = "userID";
    private static final String PARAM_NAME = "name";
    private static final String PARAM_EMAIL = "email";
    private static final String PARAM_PASSWORD1 = "password1";
    private static final String PARAM_PASSWORD2 = "password2";

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
            addInfoMessage("User " + id + " (" + name + ") created successfully.");
        } catch (AccessManagementException e) {
            log.error(e, e);
            throw new UsecaseException(e.getMessage(), e);
        }
    }

    public boolean checkPreconditions() throws UsecaseException {
        String id = getParameterAsString(PARAM_USER_ID);
        String name = getParameterAsString(PARAM_NAME);
        String email = getParameterAsString(PARAM_EMAIL);
        String password1 = getParameterAsString(PARAM_PASSWORD1);
        String password2 = getParameterAsString(PARAM_PASSWORD2);
        
        if (id == null || id.length()==0) {
            this.addError("Please enter a user ID.");
            return false;
        } else {
            UserManager userManager = getRealm().getIdentityManager().getUserManager();
            try {
                if (userManager.existsUser(id)) {
                    this.addError("User with this ID exists already.");
                    return false;
                }
            } catch (AccessManagementException e) {
                throw new UsecaseException(e.getMessage(), e);
            }
        }
        if (name == null || name.length()==0) {
            this.addError("Please enter a user name.");
            return false;
        }
        if (email == null || email.length()==0) {
            this.addError("Please enter an email address.");
            return false;
        }
        if (password1 == null || password1.length() < 6) {
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
