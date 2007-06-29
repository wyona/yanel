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
import org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource;
import org.wyona.yanel.impl.resources.usecase.UsecaseException;

/**
 *
 */
public class DeleteGroupResource extends ExecutableUsecaseResource {

    private static Category log = Category.getInstance(DeleteGroupResource.class);
    
    private static final String PARAM_GROUP_ID = "groupID";

    public void execute() throws UsecaseException {
        try {
            String id = getParameterAsString(PARAM_GROUP_ID);
            if (log.isDebugEnabled()) {
                log.debug("deleting group: " + id);
            }
            GroupManager groupManager = getRealm().getIdentityManager().getGroupManager();
            groupManager.removeGroup(id);
            addInfoMessage("Group " + id + " deleted successfully.");
        } catch (AccessManagementException e) {
            log.error(e, e);
            throw new UsecaseException(e.getMessage(), e);
        }
    }
    
    public Group getGroup() throws AccessManagementException {
        String id = getParameterAsString(PARAM_GROUP_ID);
        GroupManager groupManager = getRealm().getIdentityManager().getGroupManager();
        Group group = groupManager.getGroup(id);
        return group;
    }

}
