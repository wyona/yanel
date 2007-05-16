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
package org.wyona.yanel.impl.workflow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Category;
import org.wyona.security.core.api.Identity;
import org.wyona.security.core.api.IdentityMap;
import org.wyona.security.core.api.Role;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.WorkflowableV1;
import org.wyona.yanel.core.workflow.Condition;
import org.wyona.yanel.core.workflow.Workflow;
import org.wyona.yanel.core.workflow.WorkflowException;

public class RoleCondition implements Condition {
    
    private static Category log = Category.getInstance(RoleCondition.class);
    
    public static String IDENTITY_MAP_KEY = "identity-map";

    protected Role role;

    public void setExpression(String expression) {
        this.role = new Role(expression);
    }

    public boolean isComplied(WorkflowableV1 workflowable, Workflow workflow, String revision)
            throws WorkflowException {
        // TODO: the cast should not be necessary, the workflowable interface should 
        //       extend a resource interface
        Resource resource = ((Resource)workflowable);
        HttpServletRequest request = resource.getRequest();
        try {
            // TODO: it should be possible to get the identity from the framework,
            //       without knowledge duplication
            Identity identity = getIdentity(request, resource.getRealm().getID());
            
            if (identity == null) {
                identity = new Identity();
            }
            
            boolean authorized;
            authorized = resource.getRealm().getPolicyManager().authorize(resource.getPath(), identity, this.role);
            if (log.isDebugEnabled()) {
                log.debug("RoleCondition.isComplied():");
                log.debug("   resource : " + resource.getPath());
                log.debug("   identity : " + identity);
                log.debug("   role     : " + this.role.getName());
                log.debug("   result   : " + authorized);
            }
            return authorized;
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }

    /**
     * Gets the identity from the session associated with the given request.
     * @param request
     * @return identity or null if there is no identity in the session for the current
     *                  realm or if there is no session at all
     */
    private Identity getIdentity(HttpServletRequest request, String realmID) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null) {
            IdentityMap identityMap = (IdentityMap)session.getAttribute(IDENTITY_MAP_KEY);
            if (identityMap != null) {
                return (Identity)identityMap.get(realmID);
            }
        }
        return null;
    }

}