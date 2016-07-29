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
package org.wyona.yanel.core.workflow.impl;

import java.util.ArrayList;

import org.wyona.yanel.core.workflow.Transition;
import org.wyona.yanel.core.workflow.Workflow;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 */
public class WorkflowImpl implements Workflow {

    private static Logger log = LogManager.getLogger(WorkflowImpl.class);
    
    private String[] states;
    private String initialState;
    private Transition[] transitions;

    public WorkflowImpl() {
    }
    
    public String getInitialState() {
        return this.initialState;
    }
    
    public void setInitialState(String state) {
        this.initialState = state;
    }
    
    public String[] getStates() {
        return this.states;
    }
    
    public void setStates(String[] states) {
        this.states = states;
    }

    /**
     * @see org.wyona.yanel.core.workflow.Workflow#getTransition(String)
     */
    public Transition getTransition(String id) {
        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i].getID().equals(id)) {
                return this.transitions[i];
            }
        }
        log.error("No such transition '" + id + "'!");
        return null;
    }

    /**
     * @see org.wyona.yanel.core.workflow.Workflow#getTransitions()
     */
    public Transition[] getTransitions() {
        return this.transitions;
    }
    
    public void setTransitions(Transition[] transitions) {
        this.transitions = transitions;
    }

    public Transition[] getLeavingTransitions(String state) {
        ArrayList leavingTransitions = new ArrayList();
        for (int i = 0; i < transitions.length; i++) {
            if (transitions[i].getSourceState().equals(state)) {
                leavingTransitions.add(transitions[i]);
            }
        }
        return (Transition[])leavingTransitions.toArray(new Transition[leavingTransitions.size()]);
    }
}
