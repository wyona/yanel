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

import org.apache.log4j.Category;
import org.wyona.security.core.api.Identity;

/**
 * The environment stores items which belong to the environment from which a resource
 * is called, i.e. the request, the response, the identity, and the area.
 */
public class Environment {

    private static Category log = Category.getInstance(Environment.class);

    private HttpServletRequest request;
    private HttpServletResponse response;
    private Identity identity;
    private String area;
    
    public Environment(HttpServletRequest request, HttpServletResponse response, Identity identity, String area) {
        this.request = request;
        this.response = response;
        this.identity = identity;
        this.area = area;
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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
