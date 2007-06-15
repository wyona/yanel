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

package org.wyona.yanel.core.api.attributes;

import java.util.Date;

import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.workflow.WorkflowException;

/**
 * DEV (not released yet, this interface still might change ...)
 * &lt;br/&gt;
 * A workflow is composed of states and transitions. Each revision of a workflowable resource 
 * has a state, which can be changed by performing transitions.
 * Transitions may be bound to conditions or actions, but this is implementation specific.
 * Further, a workflowable resource can be live, or not live. If it is live, it must
 * provide a live-view.
 * 
 * The workflow information of a resource can be stored in two types of properties:
 * &lt;ul&gt;
 *   &lt;li&gt; 
 *     workflow state: property which belongs to a revision.
 *   &lt;/li&gt; 
 *   &lt;li&gt; 
 *     workflow variable: property which belongs to a resource (shared among all revisions).
 *   &lt;/li&gt; 
 * &lt;/ul&gt; 
 * 
 * Typically, the workflow state of a revision is something like draft/approved.
 * A workflow variable may e.g. store the revision number of the live revision.
 */
public interface WorkflowableV1 {

    /**
     * Perform the transition with the given id to the indicated revision.
     * @param transitionID
     * @param revision
     * @throws WorkflowException
     */
    void doTransition(String transitionID, String revision) throws WorkflowException;

    /**
     * Indicates whether this resource is live or not.
     * @return true if this resource is live, false otherwise.
     * @throws WorkflowException
     */
    boolean isLive() throws WorkflowException;
    
    /**
     * Gets the view of the live version of this resource.
     * @param viewid
     * @return live view
     * @throws Exception
     */
    View getLiveView(String viewid) throws Exception;
    
    /**
     * Gets the workflow state of the given revision.
     * @param revision
     * @return workflow state or null if the revision has no workflow state
     * @throws WorkflowException
     */
    String getWorkflowState(String revision) throws WorkflowException;
    
    /**
     * Sets the workflow state of the given revision.
     * @param state
     * @param revision
     * @throws WorkflowException
     */
    void setWorkflowState(String state, String revision) throws WorkflowException;
    
    /**
     * Gets the workflow date of the given revision, that is the date when
     * the state has changed.
     * @param revision
     * @return workflow date or null if the revision has no workflow date
     * @throws WorkflowException
     */
    Date getWorkflowDate(String revision) throws WorkflowException;
    
    
    /**
     * Gets the workflow variable with the given name.
     * @param name
     * @return workflow variable or null if this resource has no workflow variable with the given name.
     * @throws WorkflowException
     */
    String getWorkflowVariable(String name) throws WorkflowException;
    
    /**
     * Sets the workflow variable with the given name.
     * @param name
     * @param value
     * @throws WorkflowException
     */
    void setWorkflowVariable(String name, String value) throws WorkflowException;
    
    /**
     * Removes the workflow variable with the given name.
     * @param name
     * @throws WorkflowException
     */
    void removeWorkflowVariable(String name) throws WorkflowException;
    
    /**
     * Gets the introspection xml containing the workflow information.
     * Since workflow is tied to versioning, this xml also contains the revision
     * information.
     * @return workflow introspection xml
     * @throws WorkflowException
     */
    String getWorkflowIntrospection() throws WorkflowException;
    
    //boolean canDoTransition(String transitionID) throws WorkflowException;
    //String[] getPossibleTransitions() throws WorkflowException;
}

