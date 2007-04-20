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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wyona.yanel.core.api.attributes.TranslatableV1;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.util.ResourceAttributeHelper;

import org.apache.log4j.Category;

/**
 *
 */
public abstract class Resource {

    private static Category log = Category.getInstance(Resource.class);

    protected ResourceTypeDefinition rtd;
    protected ResourceTypeIdentifier rti;
    protected ResourceConfiguration rc;
    protected Yanel yanel;
    private String path;
    protected Realm realm;
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected Map parameters;

    /**
     *
     */
    public Resource() {
        rtd = null;
        this.parameters = new HashMap();
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
    
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Realm getRealm() {
        return realm;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    /**
     * @deprecated
     * @see #getConfiguration
     */
    public ResourceTypeIdentifier getRTI() {
        log.warn("DEPRECATED: see #getConfiguration");
        return rti;
    }

    /**
     * @deprecated
     * @see #setConfiguration
     */
    public void setRTI(ResourceTypeIdentifier rti) {
        log.warn("DEPRECATED: see #setConfiguration");
        this.rti = rti;
    }

    /**
     * Set resource configuration
     */
    public void setConfiguration(ResourceConfiguration rc) {
        this.rc = rc;
    }

    /**
     * Get resource configuration
     */
    public ResourceConfiguration getConfiguration() {
        return rc;
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

    /**
     * Gets the parameter map of this resource.
     * @return map with parameter names as keys and parameter values as values.
     */
    public Map getParameters() {
        return parameters;
    }

    /**
     * Sets the parameter map of this resource.
     * If a parameter map already exists, it will be replaced.
     * @param parameters map with parameter names as keys and parameter values as values.
     */
    public void setParameters(Map parameters) {
        this.parameters = parameters;
    }
    
    /**
     * Gets the parameter with the given name.
     * @param name
     * @return parameter object or null if no parameter with this name exists.
     */
    public Object getParameter(String name) {
        return this.parameters.get(name);
    }

    /**
     * Gets the parameter with the given name as a string.
     * @param name
     * @return parameter string or null if no parameter with this name exists.
     */
    public String getParameterAsString(String name) {
        return this.parameters.get(name).toString();
    }

    /**
     * Sets the parameter with the given name and value.
     * @param name
     * @param value
     */
    public void setParameter(String name, Object value) {
        this.parameters.put(name, value);
    }

    /**
     * Get language with the following priorization: 
     * 1) yanel.meta.language query string parameter 
     * 2) Translation Manager (if translatable)
     * 3) Resource Configuration property 
     * 4) Accept-Language header
     * 5) Realm default language
     * 6) Default "en"
     */
    public String getRequestedLanguage() throws Exception {
        // TODO: Make this reusable. Also see org/wyona/yanel/servlet/YanelServlet.java
        String language = getRequest().getParameter("yanel.meta.language");
        
        if (language == null && ResourceAttributeHelper.hasAttributeImplemented(this, 
                "Translatable", "1")) {
            // get language from translation manager: 
            language = ((TranslatableV1)this).getLanguage(); 
        }

        if (language == null) {
            ResourceConfiguration rc = getConfiguration();
            if (rc != null) {
                language = rc.getProperty("language");
            }
        }

        if (language == null) {
            language = getRequest().getHeader("Accept-Language");
            if (language != null) {
                if (language.indexOf(",") > 0) {
                    language = language.substring(0, language.indexOf(","));
                }
                int dashIndex = language.indexOf("-");
                if (dashIndex > 0) {
                    language = language.substring(0, dashIndex);
                }
            }
        }
        
        if (language == null) {
            language = getRealm().getDefaultLanguage();
        }
        
        if (language == null || language.length() == 0) {
            language = "en";
        }
        
        return language;
    }

}
