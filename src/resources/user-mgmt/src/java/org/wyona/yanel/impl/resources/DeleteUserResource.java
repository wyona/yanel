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
import org.wyona.security.core.api.User;
import org.wyona.security.core.api.UserManager;
import org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource;
import org.wyona.yanel.impl.resources.usecase.UsecaseException;

/**
 *
 */
public class DeleteUserResource extends ExecutableUsecaseResource {

    private static Category log = Category.getInstance(DeleteUserResource.class);
    
    private static final String PARAM_USER_ID = "userID";

    public void execute() throws UsecaseException {
        try {
            String id = getParameterAsString(PARAM_USER_ID);
            if (log.isDebugEnabled()) {
                log.debug("deleting user: " + id);
            }
            UserManager userManager = getRealm().getIdentityManager().getUserManager();
            userManager.removeUser(id);
            addInfoMessage("User " + id + " deleted successfully.");
        } catch (AccessManagementException e) {
            log.error(e, e);
            throw new UsecaseException(e.getMessage(), e);
        }
    }
    
    public User getUser() throws AccessManagementException {
        String id = getParameterAsString(PARAM_USER_ID);
        UserManager userManager = getRealm().getIdentityManager().getUserManager();
        User user = userManager.getUser(id);
        return user;
    }

}
