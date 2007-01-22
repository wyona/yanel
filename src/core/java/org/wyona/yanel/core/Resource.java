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
import org.wyona.yanel.core.map.Realm;

/**
 *
 */
public abstract class Resource {

    protected ResourceTypeDefinition rtd;
    protected ResourceTypeIdentifier rti;
    protected ResourceConfiguration rc;
    protected Yanel yanel;
    private Path path;
    protected Realm realm;
    protected HttpServletRequest request;
    protected HttpServletResponse response;

    /**
     *
     */
    public Resource() {
        rtd = null;
    }

    /**
     *
     */
    public void setRTD(ResourceTypeDefinition rtd) {
        this.rtd = rtd;
    }

    /**
     *
     */
    public ResourceTypeDefinition getRTD() {
        return rtd;
    }

    /**
     *
     */
    public void setYanel(Yanel yanel) {
        this.yanel = yanel;
    }

    /**
     *
     */
    public Yanel getYanel() {
        return yanel;
    }

    /**
     *
     */
    public String getResourceTypeUniversalName() {
        return rtd.getResourceTypeUniversalName();
    }

    /**
     *
     */
    public String getResourceTypeLocalName() {
        return rtd.getResourceTypeLocalName();
    }

    /**
     *
     */
    public String getResourceTypeNamespace() {
        return rtd.getResourceTypeNamespace();
    }
    
    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Realm getRealm() {
        return realm;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    public ResourceTypeIdentifier getRTI() {
        return rti;
    }

    public void setRTI(ResourceTypeIdentifier rti) {
        this.rti = rti;
    }

    public void setConfiguration(ResourceConfiguration rc) {
        this.rc = rc;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

}
