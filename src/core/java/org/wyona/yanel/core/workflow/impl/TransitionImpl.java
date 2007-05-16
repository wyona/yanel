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

import java.util.HashMap;
import java.util.Set;

import org.wyona.yanel.core.workflow.Action;
import org.wyona.yanel.core.workflow.Condition;
import org.wyona.yanel.core.workflow.Transition;

public class TransitionImpl implements Transition {
    private String id;
    private String sourceState;
    private String destinationState;
    private Condition[] conditions;
    private Action[] actions;
    private HashMap descriptions;
    
    public TransitionImpl(String id, String from, String to) {
        this.id = id;
        this.sourceState = from;
        this.destinationState = to;
        this.descriptions = new HashMap();
    }
    
    public String getID() {
        return id;
    }
    
    public void setID(String id) {
        this.id = id;
    }
    
    public String getDestinationState() {
        return destinationState;
    }
    
    public void setDestinationState(String destinationState) {
        this.destinationState = destinationState;
    }
    
    public String getSourceState() {
        return sourceState;
    }
    
    public void setSourceState(String sourceState) {
        this.sourceState = sourceState;
    }

    public Action[] getActions() {
        return actions;
    }

    public void setActions(Action[] actions) {
        this.actions = actions;
    }

    public Condition[] getConditions() {
        return conditions;
    }

    public void setConditions(Condition[] conditions) {
        this.conditions = conditions;
    }

    public String getDescription(String language) {
        return (String)this.descriptions.get(language);
    }

    public String[] getDescriptionLanguages() {
        Set languages = this.descriptions.keySet();
        return (String[])languages.toArray(new String[languages.size()]);
    }
    
    public void addDescription(String description, String language) {
        this.descriptions.put(language, description);
    }
}