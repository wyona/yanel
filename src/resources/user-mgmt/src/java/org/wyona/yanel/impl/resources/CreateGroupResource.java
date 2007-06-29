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
import org.wyona.security.core.api.GroupManager;
import org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource;
import org.wyona.yanel.impl.resources.usecase.UsecaseException;


/**
 *
 */
public class CreateGroupResource extends ExecutableUsecaseResource {

    private static Category log = Category.getInstance(CreateGroupResource.class);

    private static final String PARAM_GROUP_ID = "groupID";
    private static final String PARAM_NAME = "name";

    public void execute() throws UsecaseException {
        GroupManager groupManager = getRealm().getIdentityManager().getGroupManager();
        String id = getParameterAsString(PARAM_GROUP_ID);
        String name = getParameterAsString(PARAM_NAME);
        try {
            if (log.isDebugEnabled()) {
                log.debug("creating group: " + id + " " + name);
            }
            groupManager.createGroup(id, name);
            addInfoMessage("Group " + id + " (" + name + ") created successfully.");
        } catch (AccessManagementException e) {
            log.error(e, e);
            throw new UsecaseException(e.getMessage(), e);
        }
    }

    public boolean checkPreconditions() throws UsecaseException {
        String id = getParameterAsString(PARAM_GROUP_ID);
        String name = getParameterAsString(PARAM_NAME);
        
        if (id == null || id.length()==0) {
            this.addError("Please enter a group ID.");
            return false;
        } else {
            GroupManager groupManager = getRealm().getIdentityManager().getGroupManager();
            try {
                if (groupManager.existsGroup(id)) {
                    this.addError("Group with this ID exists already.");
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
        return true;
    }

}
