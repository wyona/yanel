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
 * 
 * A workflow state belongs to a revision.
 * A workflow variable belongs to a resource.
 */
public interface WorkflowableV1 {

    void doTransition(String transitionID, String revision) throws WorkflowException;

    boolean isLive() throws WorkflowException;
    View getLiveView(String viewid) throws Exception;
    
    String getWorkflowState(String revision) throws WorkflowException;
    void setWorkflowState(String state, String revision) throws WorkflowException;
    Date getWorkflowDate(String revision) throws WorkflowException;
    
    String getWorkflowVariable(String name) throws WorkflowException;
    void setWorkflowVariable(String name, String value) throws WorkflowException;
    void removeWorkflowVariable(String name) throws WorkflowException;
    
    String getWorkflowIntrospection() throws WorkflowException;
    
    //boolean canDoTransition(String transitionID) throws WorkflowException;
    //String[] getPossibleTransitions() throws WorkflowException;
}

