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

import org.apache.log4j.Category;
import org.wyona.security.core.api.Identity;
import org.wyona.security.core.api.Role;
import org.wyona.security.core.api.Usecase;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.WorkflowableV1;
import org.wyona.yanel.core.workflow.Condition;
import org.wyona.yanel.core.workflow.Workflow;
import org.wyona.yanel.core.workflow.WorkflowException;

/**
 * @deprecated Use UsecaseCondition instead
 */
public class RoleCondition implements Condition {
    
    private static Category log = Category.getInstance(RoleCondition.class);
    
    protected Role role;

    public void setExpression(String expression) {
        this.role = new Role(expression);
    }

    public boolean isComplied(WorkflowableV1 workflowable, Workflow workflow, String revision)
            throws WorkflowException {
        // TODO: the cast should not be necessary, the workflowable interface should 
        //       extend a resource interface
        Resource resource = ((Resource)workflowable);
        try {
            Identity identity = resource.getEnvironment().getIdentity();
            
            if (identity == null) {
                identity = new Identity();
            }
            
            boolean authorized;
            authorized = resource.getRealm().getPolicyManager().authorize(resource.getPath(), identity, new Usecase(this.role.getName()));
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

}
