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

package org.wyona.yanel.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wyona.security.core.api.Identity;

import org.apache.log4j.Logger;

/**
 * The environment stores items which belong to the environment from which a resource
 * is called, i.e. the request, the response, the identity, the state of view, and the resource container path.
 */
public class Environment {
    private static Logger log = Logger.getLogger(Environment.class);

    private HttpServletRequest request;
    private HttpServletResponse response;
    private Identity identity;
    private String sov;
    private String rcp;
    
    public Environment(HttpServletRequest request, HttpServletResponse response, Identity identity, String stateOfView, String resourceContainerPath) {
        setRequest(request);
        setResponse(response);
        setIdentity(identity);
        setStateOfView(stateOfView);
        setResourceContainerPath(resourceContainerPath);
    }

    public Identity getIdentity() {
        return identity;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }
    
    public String getResourceContainerPath() {
    	return rcp;
    }
    
    public void setResourceContainerPath(String path) {
        this.rcp = path;
    }

    /**
     * Set state of view
     */
    public void setStateOfView(String state) {
        if (log.isDebugEnabled()) { // Do not enable this, because the string comparison below is time consuming
            if (!(StateOfView.LIVE.equals(state) || StateOfView.AUTHORING.equals(state))) {
                log.warn("No such state '" + state + "' explicitely supported by Yanel!");
            } else {
                log.debug("State of view: " + state);
            }
        }
        this.sov = state;
    }

    /**
     * Get state of view (LIVE, AUTHORING)
     */
    public String getStateOfView() {
        return sov;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}
