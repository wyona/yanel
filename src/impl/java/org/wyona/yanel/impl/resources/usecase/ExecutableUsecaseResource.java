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

package org.wyona.yanel.impl.resources.usecase;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Category;
import org.wyona.yanel.core.attributes.viewable.View;

/**
 * The standard executable usecase works as follows:
 *   - display the "default" view
 *     this view has a form with a submit button and a cancel button.
 *   - if the user clicks submit, the checkPreconditions() method is executed.
 *       - if there are any errors, the "default" view is displayed again, 
 *         showing the error messages.
 *       - if everything is ok, the execute() method is called
 *           - display the "done" view
 *   - if the user cancels, call the cancel() method and then show the "cancel" view.
 */
public class ExecutableUsecaseResource extends UsecaseResource implements Executable {

    private static Category log = Category.getInstance(ExecutableUsecaseResource.class);
    
    protected static final String VIEW_DEFAULT = "default";
    protected static final String VIEW_DONE = "done";
    protected static final String VIEW_CANCEL = "cancel";
    
    protected static final String PARAM_SUBMIT = "submit";
    protected static final String PARAM_CANCEL = "cancel";

    protected List infoMessages;
    protected List errorMessages;

    /**
     *
     */
    public ExecutableUsecaseResource() {
        this.infoMessages = new LinkedList();
        this.errorMessages = new LinkedList();
    }

    protected View processUsecase(String viewID) throws UsecaseException {
        if (getParameter(PARAM_SUBMIT) != null) {
            if (!checkPreconditions() || hasErrors()) {
                return generateView(VIEW_DEFAULT);
            }
            execute();
            return generateView(VIEW_DONE);
        } else if (getParameter(PARAM_CANCEL) != null) {
            cancel();
            return generateView(VIEW_CANCEL);
        } else {
            return generateView(viewID); // this will show the default view if the param is not set
        }
    }

    public void cancel() throws UsecaseException {
        // implement in subclass
    }

    public boolean checkPreconditions() throws UsecaseException {
        // implement in subclass
        return true;
    }

    public void execute() throws UsecaseException {
        // implement in subclass
    }

    public boolean hasErrors() {
        return this.errorMessages.size() > 0;
    }
    
    public String getErrorMessages() {
        Iterator iter = this.errorMessages.iterator();
        StringBuffer buf = new StringBuffer();
        while (iter.hasNext()) {
            String msg = (String)iter.next();
            buf.append(msg);
        }
        return buf.toString();
    }

    public void addError(String error) {
        this.errorMessages.add(error);
    }

    public boolean hasInfoMessages() {
        return this.infoMessages.size() > 0;
    }
    
    public String getInfoMessages() {
        Iterator iter = this.infoMessages.iterator();
        StringBuffer buf = new StringBuffer();
        while (iter.hasNext()) {
            String msg = (String)iter.next();
            buf.append(msg);
        }
        return buf.toString();
    }

    public void addInfoMessage(String msg) {
        this.infoMessages.add(msg);
    }

}
