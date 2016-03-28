/*
 * Copyright 2006 - 2016 Wyona
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.wyona.security.core.api.AccessManagementException;
import org.wyona.security.core.api.User;
import org.wyona.security.core.api.UserManager;
import org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource;
import org.wyona.yanel.impl.resources.usecase.UsecaseException;

/**
 *
 */
public class DeleteUserResource extends ExecutableUsecaseResource {

    private static Logger log = LogManager.getLogger(DeleteUserResource.class);
    
    private static final String PARAM_USER_ID = "userID";

    public void execute() throws UsecaseException {
        try {
            String id = getParameterAsString(PARAM_USER_ID);
            if (log.isDebugEnabled()) {
                log.debug("deleting user: " + id);
            }
            UserManager userManager = getRealm().getIdentityManager().getUserManager();

            User user = userManager.getUser(id);
            
            userManager.removeUser(id);

            org.wyona.security.core.UserHistory history = user.getHistory();
            history.addEntry(new org.wyona.security.core.UserHistory().new HistoryEntry(new java.util.Date(), "user-removed", "successful"));
            user.setHistory(history);

            addInfoMessage("User " + id + " deleted successfully.");

            deleteUserProfileAccessPolicy(id);
        } catch (AccessManagementException e) {
            log.error(e, e);
            throw new UsecaseException(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e, e);
            throw new UsecaseException(e.getMessage(), e);
        }
    }

    /**
     * Delete user profile access policy
     * @param id User ID
     */
    private void deleteUserProfileAccessPolicy(String id) throws Exception {
        org.wyona.security.core.api.PolicyManager policyManager = getRealm().getPolicyManager();
        // TODO: Replace "/users" by org.wyona.yanel.servlet.YanelGlobalResourceTypeMatcher#usersPathPrefix
        policyManager.removePolicy("/" + getYanel().getReservedPrefix() + "/users/" + id + ".html");
    }
    
    /**
     *
     */
    public User getUser() throws AccessManagementException {
        String id = getParameterAsString(PARAM_USER_ID);
        UserManager userManager = getRealm().getIdentityManager().getUserManager();
        User user = userManager.getUser(id);
        return user;
    }
}
