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
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.api.attributes.WorkflowableV1;
import org.wyona.yanel.core.attributes.versionable.RevisionInformation;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.util.DateUtil;
import org.wyona.yanel.core.util.ResourceAttributeHelper;
import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.Property;
import org.wyona.yarep.core.Revision;

/**
 * This class provides some helper methods related to workflow for resources 
 * which are WorkflowableV1, VersionableV2, and ViewableV2 and which implement workflow
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
    
    /**
     * Get revision which is marked as live
     */
    public static String getLiveRevision(Resource resource) throws WorkflowException {
        try {
            WorkflowableV1 workflowable = (WorkflowableV1)resource;
            if (getWorkflow(resource) == null) {
                RevisionInformation[] revisions = ((VersionableV2)resource).getRevisions();
                if (revisions == null || revisions.length == 0) {
                    return null;
                } else {
                    return revisions[revisions.length-1].getName();
                }
            } else {
                return workflowable.getWorkflowVariable(LIVE_REVISION_PROPERTY);
            }
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }

    /**
     * Indicates whether the given resource is live, i.e. if the workflow variable
     * &quot;live-revision&quot; has a defined value.
     * If a resource does not have an associated workflow, it is considered as live.
     * @param resource
     * @return true if live, false otherwise
     * @throws WorkflowException
     */
    public static boolean isLive(Resource resource) throws WorkflowException {
        try {
            WorkflowableV1 workflowable = (WorkflowableV1)resource;
            if (getWorkflow(resource) == null || workflowable.getWorkflowVariable(LIVE_REVISION_PROPERTY) != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }
    
    public static View getLiveView(Resource resource, String viewid) throws Exception {
        if (((WorkflowableV1)resource).isLive()) {
            String liveRevision = getLiveRevision(resource);
            if (liveRevision != null) {
                return ((VersionableV2)resource).getView(viewid, liveRevision);
            } else {
                // if there are no revisions, show the normal view:
                return ((ViewableV2)resource).getView(viewid);
            }
        } else {
            return null;
        }
    }

    public static Workflow getWorkflow(Resource resource) throws WorkflowException {
        try {
            String workflowSchema = resource.getResourceConfigProperty("workflow-schema");
            if (workflowSchema == null) {
                return null;
            } else {
                WorkflowBuilder builder = new WorkflowBuilder();
                InputStream stream = resource.getRealm().getRepository().getNode(workflowSchema).getInputStream();
                // TODO: cache of workflow
                return builder.buildWorkflow(stream);
            }
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }
    
    /**
     * Get introspection part re workflow and versioning
     */
    public static String getWorkflowIntrospection(Resource resource) throws WorkflowException {
        try {
            if (!ResourceAttributeHelper.hasAttributeImplemented(resource, "Versionable", "2")) throw new WorkflowException("Resource '" + resource.getClass().getName() + "'  has not VersionableV2 interface implemented!");

            RevisionInformation[] revisions = ((VersionableV2)resource).getRevisions();
            WorkflowableV1 workflowable = (WorkflowableV1)resource;
            String liveRevision = getLiveRevision(resource);

            if (revisions != null && revisions.length > 0) {
                StringBuffer sb = new StringBuffer();
                sb.append("<versions xmlns=\"http://www.wyona.org/neutron/2.0\">");
                for (int i = revisions.length - 1; i >= 0; i--) {
                    
                    sb.append("<version url=\"?yanel.resource.revision=" + revisions[i].getName() + "\">");
                    sb.append("<comment>" + revisions[i].getComment() + "</comment>");
                    sb.append("<date>" + revisions[i].getDate() + "</date>");
                    sb.append("<user>" + revisions[i].getUser() + "</user>");
                    sb.append("<revision>" + revisions[i].getName() + "</revision>");
                    
                    Workflow workflow = getWorkflow(resource);
                    if (workflow != null) {
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
                            state += " (LIVE)"; // TODO: this is a hack
                        }
                        sb.append("<state date=\"" + date + "\">" + state + "</state>");
                        
                        sb.append("<transitions>");
transitions:            for (int j = 0; j < transitions.length; j++) {
                            Condition[] conditions = transitions[j].getConditions();
                            for (int k = 0; k < conditions.length; k++) {
                                if (!conditions[k].isComplied(workflowable, workflow, revisions[i].getName())) {
                                    log.warn("Condition (" + conditions[k].getClass().getName() + ") failed for workflow transition '" + transitions[j].getID() + "' (Revision: " + revisions[i].getName() + ")");
                                    continue transitions; // jump to next transition
                                }
                            }
                            sb.append("<transition id=\"" + transitions[j].getID() + "\" to=\"" + transitions[j].getDestinationState() + "\" url=\"?yanel.resource.workflow.transition=" + transitions[j].getID() + "&amp;yanel.resource.revision=" + revisions[i].getName() + "\" method=\"POST\">");
                            String[] languages = transitions[j].getDescriptionLanguages();
                            for (int l = 0; l < languages.length; l++) {
                                String description = transitions[j].getDescription(languages[l]);
                                sb.append("<description xml:lang=\"" + languages[l] + "\">" + description + "</description>");
                            }
                            sb.append("</transition>");
                        }
                        sb.append("</transitions>");
                        sb.append("<!-- NOTE: Workflow history not implemented by Yanel yet! -->");
                        sb.append("<history/>");
                        sb.append("</workflow>");
                    }
                    
                    sb.append("</version>");
                }
                sb.append("</versions>");
                return sb.toString();
            } else {
                return "<!-- No revisions! -->";
            }
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }

    /**
     *
     */
    public static String getWorkflowVariable(Resource resource, String name) throws WorkflowException {
        try {
            return getWorkflowVariable(resource.getRealm().getRepository().getNode(resource.getPath()), name);
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }

    /**
     *
     */
    public static String getWorkflowVariable(Node node, String name) throws WorkflowException {
        try {
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

    /**
     *
     */
    public static void setWorkflowVariable(Resource resource, String name, String value) throws WorkflowException {
        try {
            setWorkflowVariable(resource.getRealm().getRepository().getNode(resource.getPath()), name, value);
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }

    /**
     *
     */
    public static void setWorkflowVariable(Node node, String name, String value) throws WorkflowException {
        try {
            node.setProperty(name, value);
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }
    
    public static void removeWorkflowVariable(Resource resource, String name) throws WorkflowException {
        try {
            removeWorkflowVariable(resource.getRealm().getRepository().getNode(resource.getPath()), name);
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }
    
    public static void removeWorkflowVariable(Node node, String name) throws WorkflowException {
        try {
            node.removeProperty(name);
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }

    /**
     *
     */
    public static String getWorkflowState(Resource resource, String revision) throws WorkflowException {
        try {
            return getWorkflowState(resource.getRealm().getRepository().getNode(resource.getPath()), revision);
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }

    /**
     *
     */
    public static String getWorkflowState(Node node, String revision) throws WorkflowException {
        try {
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

    /**
     *
     */
    public static void setWorkflowState(Resource resource, String state, String revision) throws WorkflowException {
        try {
            setWorkflowState(resource.getRealm().getRepository().getNode(resource.getPath()), state, revision);
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }

    /**
     *
     */
    public static void setWorkflowState(Node node, String state, String revision) throws WorkflowException {
        try {
            Revision rev = node.getRevision(revision); 
            rev.setProperty(WORKFLOW_STATE_PROPERTY, state);
            rev.setProperty(WORKFLOW_DATE_PROPERTY, new Date());
            // TODO: write workflow history
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }

    /**
     *
     */
    public static Date getWorkflowDate(Resource resource, String revision) throws WorkflowException {
        try {
            return getWorkflowDate(resource.getRealm().getRepository().getNode(resource.getPath()), revision);
        } catch (Exception e) {
            log.error(e, e);
            throw new WorkflowException(e.getMessage(), e);
        }
    }

    /**
     *
     */
    public static Date getWorkflowDate(Node node, String revision) throws WorkflowException {
        try {
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
