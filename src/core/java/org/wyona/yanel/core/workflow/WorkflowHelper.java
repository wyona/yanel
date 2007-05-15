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
package org.wyona.yanel.core.workflow;

import java.io.InputStream;
import java.util.Date;

import org.apache.log4j.Category;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.VersionableV2;
import org.wyona.yanel.core.api.attributes.WorkflowableV1;
import org.wyona.yanel.core.attributes.versionable.RevisionInformation;
import org.wyona.yanel.core.util.DateUtil;
import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.Property;
import org.wyona.yarep.core.Revision;

/**
 * This class provides some helper methods related to workflow for resources 
 * which are WorkflowableV1 and VersionableV2 and which implement workflow
 * according to the following conditions:
 * - there is a resource config property 'workflow-schema' which points to the schema
 * - the workflow information is stored as properties of a repository node in the repository
 *   of the resource with the path of the resource.
 *
 */
public class WorkflowHelper {
    
    private static Category log = Category.getInstance(WorkflowHelper.class);

    protected static final String LIVE_REVISION_PROPERTY = "live-revision";
    protected static final String WORKFLOW_DATE_PROPERTY = "workflow-date";
    protected static final String WORKFLOW_STATE_PROPERTY = "workflow-state";

    public static void doTransition(Resource resource, String transitionID, String revision) 
            throws WorkflowException {
        boolean foundTransition = false;
        String currentState = null;
        WorkflowableV1 workflowable = (WorkflowableV1)resource;
        Workflow workflow = getWorkflow(resource);
        currentState = workflowable.getWorkflowState(revision);
        if (currentState == null) {
            currentState = workflow.getInitialState(); 
        }
        Transition[] transitions = workflow.getLeavingTransitions(currentState);
        
        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i].getID().equals(transitionID)) {
                Condition[] conditions = transitions[i].getConditions();
                for (int j = 0; j < conditions.length; j++) {
                    if (!conditions[j].isComplied(workflowable, workflow, revision)) {
                        throw new WorkflowException("Workflow condition not complied for state " + 
                                currentState + " and transition " + transitionID);
                    }
                }
                String newState = transitions[i].getDestinationState();
                
                workflowable.setWorkflowState(newState, revision);
                
                Action[] actions = transitions[i].getActions();
                for (int j = 0; j < actions.length; j++) {
                    actions[j].execute(workflowable, workflow, revision);
                }
                
                foundTransition = true;
            }
        }
        if (!foundTransition) {
            throw new WorkflowException("Invalid workflow transition: " + transitionID + 
                    " for state: " + currentState);
        }
    }
    
    /*public static boolean canDoTransition(Resource resource, String transitionID, String revision) 
            throws WorkflowException {
        WorkflowableV1 workflowable = (WorkflowableV1)resource;
        Workflow workflow = getWorkflow(resource);
        String currentState = workflowable.getWorkflowState(revision);
        if (currentState == null) {
            currentState = workflow.getInitialState(); 
        }
        Transition[] transitions = workflow.getLeavingTransitions(currentState);
        
        for (int j = 0; j < transitions.length; j++) {
            Condition[] conditions = transitions[j].getConditions();
            for (int k = 0; k < conditions.length; k++) {
                if (!conditions[k].isComplied(workflowable, workflow, revision)) {
                    return true;
                }
            }
        }
        return false;
    }*/
    
    public static String getLiveRevision(Resource resource) throws WorkflowException {
        try {
            WorkflowableV1 workflowable = (WorkflowableV1)resource;
            return workflowable.getWorkflowVariable(LIVE_REVISION_PROPERTY);
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }

    public static boolean isLive(Resource resource) throws WorkflowException {
        try {
            WorkflowableV1 workflowable = (WorkflowableV1)resource;
            if (workflowable.getWorkflowVariable(LIVE_REVISION_PROPERTY) != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }
    
    public static Workflow getWorkflow(Resource resource) throws WorkflowException {
        try {
            String workflowSchema = resource.getResourceConfigProperty("workflow-schema");
            if (workflowSchema == null) {
                return null;
            } else {
                WorkflowBuilder builder = new WorkflowBuilder();
                InputStream stream;
                    stream = resource.getRealm().getRepository().getNode(workflowSchema).getInputStream();
                // TODO: cache of workflow
                return builder.buildWorkflow(stream);
            }
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }
    
    public static String getWorkflowIntrospection(Resource resource) throws WorkflowException {
        try {
            StringBuffer sb = new StringBuffer();
            RevisionInformation[] revisions = ((VersionableV2)resource).getRevisions();
            WorkflowableV1 workflowable = (WorkflowableV1)resource;
            String liveRevision = getLiveRevision(resource);
            if (revisions != null && revisions.length > 0) {
                sb.append("<versions>");
                for (int i = revisions.length - 1; i >= 0; i--) {
                    
                    sb.append("<version url=\"?yanel.resource.revision=" + revisions[i].getName() + "\">");
                    sb.append("<comment>" + revisions[i].getComment() + "</comment>");
                    sb.append("<date>" + revisions[i].getDate() + "</date>");
                    sb.append("<user>" + revisions[i].getUser() + "</user>");
                    sb.append("<revision>" + revisions[i].getName() + "</revision>");
                    
                    Workflow workflow = getWorkflow(resource);
                    if (workflow != null) {
                        // TODO: handle workflow==null !!!
                        String state = workflowable.getWorkflowState(revisions[i].getName());
                        if (state == null) {
                            state = workflow.getInitialState();
                        }
                        Date workflowDate = workflowable.getWorkflowDate(revisions[i].getName());
                        String date = "";
                        if (workflowDate != null) {
                            date = DateUtil.format(workflowDate);
                        }
                        
                        Transition[] transitions = workflow.getLeavingTransitions(state);
    
                        sb.append("<workflow>");
    
                        if (revisions[i].getName().equals(liveRevision)) {
                            state += "-LIVE"; // TODO: this is a hack
                        }
                        sb.append("<state date=\"" + date + "\">" + state + "</state>");
                        
                        sb.append("<transitions>");
transitions:            for (int j = 0; j < transitions.length; j++) {
                            Condition[] conditions = transitions[j].getConditions();
                            for (int k = 0; k < conditions.length; k++) {
                                if (!conditions[k].isComplied(workflowable, workflow, revisions[i].getName())) {
                                    continue transitions; // jump to next transition
                                }
                            }
                            sb.append("<transition id=\""+transitions[j].getID()+"\" to=\""+transitions[j].getDestinationState()+"\" url=\"?yanel.resource.workflow.transition="+transitions[j].getID()+"&amp;yanel.resource.revision=" + revisions[i].getName() + "\" method=\"POST\">");
                            sb.append("<description>"+transitions[j].getID()+"</description>");
                            sb.append("</transition>");
                        }
                        sb.append("</transitions>");
                        sb.append("<history/>");
                        sb.append("</workflow>");
                    }
                    
                    sb.append("</version>");
                }
                sb.append("</versions>");
                return sb.toString();
            } else {
                return "";
            }
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }

    public static String getWorkflowIntrospectionAnswer(Resource resource, String revision) throws WorkflowException {
        try {
            StringBuffer sb = new StringBuffer();
            WorkflowableV1 workflowable = (WorkflowableV1)resource;

            Workflow workflow = getWorkflow(resource);
            String state = workflowable.getWorkflowState(revision);
            if (state == null) {
                state = workflow.getInitialState();
            }
            String date = "";
            
            sb.append("<workflow xmlns=\"http://www.wyona.org/neutron/2.0\">");
            sb.append("  <state date=\"" + date + "\">" + state + "</state>");
            
            Transition[] transitions = workflow.getLeavingTransitions(state);
                
            sb.append("<transitions>");
transitions: for (int j = 0; j < transitions.length; j++) {
                Condition[] conditions = transitions[j].getConditions();
                for (int k = 0; k < conditions.length; k++) {
                    if (!conditions[k].isComplied(workflowable, workflow, revision)) {
                        continue transitions; // jump to next transition
                    }
                }
                sb.append("<transition id=\""+transitions[j].getID()+"\" to=\""+transitions[j].getDestinationState()+"\" url=\"?yanel.resource.workflow.transition="+transitions[j].getID()+"\" method=\"POST\">");
                sb.append("<description>"+transitions[j].getID()+"</description>");
                sb.append("</transition>");
            }
            sb.append("</transitions>");
            sb.append("<history/>");
            sb.append("</workflow>");
                    
            return sb.toString();
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }
    
    public static String getWorkflowVariable(Resource resource, String name) throws WorkflowException {
        try {
            Node node = resource.getRealm().getRepository().getNode(resource.getPath());
            Property property = node.getProperty(name);
            if (property != null) {
                return property.getString();
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }

    public static void setWorkflowVariable(Resource resource, String name, String value) throws WorkflowException {
        try {
            Node node = resource.getRealm().getRepository().getNode(resource.getPath());
            node.setProperty(name, value);
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }
    
    public static void removeWorkflowVariable(Resource resource, String name) throws WorkflowException {
        try {
            Node node = resource.getRealm().getRepository().getNode(resource.getPath());
            node.removeProperty(name);
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }

    public static String getWorkflowState(Resource resource, String revision) throws WorkflowException {
        try {
            Node node = resource.getRealm().getRepository().getNode(resource.getPath());
            Property stateProp = node.getRevision(revision).getProperty(WORKFLOW_STATE_PROPERTY);
            if (stateProp != null) {
                return stateProp.getString();
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }

    public static void setWorkflowState(Resource resource, String state, String revision) throws WorkflowException {
        try {
            Node node = resource.getRealm().getRepository().getNode(resource.getPath());
            Revision rev = node.getRevision(revision); 
            rev.setProperty(WORKFLOW_STATE_PROPERTY, state);
            rev.setProperty(WORKFLOW_DATE_PROPERTY, new Date());
            // TODO: write workflow history
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }

    public static Date getWorkflowDate(Resource resource, String revision) throws WorkflowException {
        try {
            Node node = resource.getRealm().getRepository().getNode(resource.getPath());
            Property dateProp = node.getRevision(revision).getProperty(WORKFLOW_DATE_PROPERTY);
            if (dateProp != null) {
                return dateProp.getDate();
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }

}