/*
 * Copyright 2011 Wyona
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

import org.wyona.yanel.core.api.attributes.WorkflowableV1;
import org.wyona.yanel.core.workflow.Action;
import org.wyona.yanel.core.workflow.Workflow;
import org.wyona.yanel.core.workflow.WorkflowException;

import org.apache.log4j.Logger;

/**
 * Send notifications via email
 */
public class EmailAction implements Action {

    private static Logger log = Logger.getLogger(EmailAction.class);

    private String expression;

    /**
     * @see org.wyona.yanel.core.workflow.Action#setExpression(String)
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * @see org.wyona.yanel.core.workflow.Action#execute(WorkflowableV1, Workflow, String)
     */
    public void execute(WorkflowableV1 workflowable, Workflow workflow, String revision) throws WorkflowException {
        String[] emailAddresses = getEmailAddresses();
        if (emailAddresses != null) {
            for (int i = 0; i < emailAddresses.length; i++) {
                log.warn("Send email to: " + emailAddresses[i]);
            }
        } else {
            throw new WorkflowException("No email addresses!");
        }
    }

    /**
     * Get email addresses
     */
    private String[] getEmailAddresses() throws WorkflowException {
        if (expression != null) {
            String[] ea = expression.split(",");
            String[] emails = new String[ea.length]; // TODO: Make more fault tolerant
            for(int i = 0; i < ea.length; i++) {
                if (ea[i].indexOf("@") < 1) {
                    throw new WorkflowException("No such email address: " + ea[i]);
                } else {
                    emails[i] = ea[i].trim();
                }
            }
            return emails;
        } else {
            return null;
        }
    }
}
