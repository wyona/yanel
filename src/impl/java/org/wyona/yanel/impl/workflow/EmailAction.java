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

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.WorkflowableV1;
import org.wyona.yanel.core.workflow.Action;
import org.wyona.yanel.core.workflow.Workflow;
import org.wyona.yanel.core.workflow.WorkflowException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Send notifications via email
 */
public class EmailAction implements Action {

    private static Logger log = LogManager.getLogger(EmailAction.class);

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
        String from = getSenderAddress(workflowable, workflow, revision);
        String subject = getSubject(workflowable, workflow, revision);
        String content = getText(workflowable, workflow, revision);
        if (emailAddresses != null) {
            for (int i = 0; i < emailAddresses.length; i++) {
                log.info("Send email to: " + emailAddresses[i]);
                try {
                    org.wyona.yanel.core.util.MailUtil.send(from, emailAddresses[i], subject, content);
                } catch(Exception e) {
                    throw new WorkflowException(e);
                }
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

    /**
     * Get from/sender address
     */
    protected String getSenderAddress(WorkflowableV1 workflowable, Workflow workflow, String revision) throws WorkflowException {
        String from = null;
        // TODO: Make from address configurable inside workflow configuration
        try {
            from = org.wyona.yanel.core.Yanel.getInstance().getAdministratorEmail();
        } catch(Exception e) {
            log.error(e, e);
        }
        if (from != null) {
            return from;
        }
        from = "contact@wyona.com";
        log.warn("From email address not configured, hence use '" + from + "' as fallback.");
        return from;
    }

    /**
     * Get subject
     * @param workflowable Resource which has implemented WorkflowableV1
     */
    protected String getSubject(WorkflowableV1 workflowable, Workflow workflow, String revision) throws WorkflowException {
        Resource resource = (Resource) workflowable;
        return "[" + resource.getRealm().getName() + "] The workflow state of '" + new java.io.File(resource.getPath()).getName() + "' has been changed to '" + workflowable.getWorkflowState(revision) + "'";
    }

    /**
     * Get text
     */
    protected String getText(WorkflowableV1 workflowable, Workflow workflow, String revision) throws WorkflowException {
        Resource resource = (Resource) workflowable;

        // TODO: We cannot use the webapp package ahead of the impl package, because of compilation order, but maybe we could copy this method to the core or impl package and set the one inside the webapp packaged to deprecated
        //String url = removeQueryString(org.wyona.yanel.servlet.Utils.getRequestURLQS(resource.getRealm(), resource.getEnvironment().getRequest(), resource.getPath(), false));
        String url = resource.getPath();

        return "Please review the workflow state change of the resource: " + url;
    }

    /**
     * @url URL which might contain a query string, e.g. 'http://127.0.0.1:8080/yanel/test/index.html?param=value'
     * @return URL without query string, e.g. 'http://127.0.0.1:8080/yanel/test/index.html'
     */
    private static String removeQueryString(String url) {
        if (url.indexOf("?") > 0) {
            return url.substring(0, url.indexOf("?"));
        } else {
            return url;
        }
    }
}
