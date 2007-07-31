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

    // TODO: make protected members private (access in subclasses via getter/setter methods)
    protected ResourceTypeDefinition rtd;
    protected ResourceTypeIdentifier rti;
    protected ResourceConfiguration rc;
    protected Yanel yanel;
    private String path;
    protected Realm realm;
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    private Environment environment;
    protected Map parameters;

    static private String DEFAULT_LANGUAGE = "en";

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

    /**
     * @deprecated use getEnvironment().getRequest() instead  
     */
    public HttpServletRequest getRequest() {
        return environment.getRequest();
    }

    /**
     * @deprecated use getEnvironment().setRequest() instead 
     */
    public void setRequest(HttpServletRequest request) {
        environment.setRequest(request);
    }

    /**
     * @deprecated use getEnvironment().getResponse() instead  
     */
    public HttpServletResponse getResponse() {
        return environment.getResponse();
    }

    /**
     * @deprecated use getEnvironment().setResponse() instead
     */
    public void setResponse(HttpServletResponse response) {
        environment.setResponse(response);
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
        Object param = this.parameters.get(name);
        if (param == null) {
            return null;
        } else {
            return param.toString();
        }
    }

    /**
     * Gets the parameter values with the given name as a string array.
     * @param name
     * @return array containing the parameter values, or empty array if parameter does not exist.
     */
    public String[] getParameterAsStringValues(String name) {
        Object param = this.parameters.get(name);
        if (param == null) {
            return new String[0];
        }
        if (param instanceof String) {
            String[] values = new String[1];
            values[0] = (String)param;
            return values;
        } else if (param instanceof String[]) {
            return (String [])param;
        }
        return null;
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
     * Get content language resp. the language of this translation (see getRequestedLanguage re localization)
     */
    public String getContentLanguage() throws Exception {
        String language = null;
        if (ResourceAttributeHelper.hasAttributeImplemented(this, "Translatable", "1")) {
            language = ((TranslatableV1)this).getLanguage(); 
        }
        if (language != null) return language;

        language = getResourceConfigProperty("language");
        if (language != null) return language;

        return getRequestedLanguage();
    }

    /**
     * Get language (localization) with the following priorization: <br><br>
     * 1) yanel.meta.language query string parameter<br> 
     * 2) Accept-Language header<br>
     * 3) Realm default language<br>
     * 4) Default "en"<br>
     */
    public String getRequestedLanguage() throws Exception {
        // TODO: Make this reusable. Also see org/wyona/yanel/servlet/YanelServlet.java

        // TODO: Use user profile setting resp. session (allow switching the locale)
	    
        // (1)
        String language = getRequest().getParameter("yanel.meta.language");
        if (language != null) return language;
        
        // (2)
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
        if (language != null) return language;

        // (3)
        language = getRealm().getDefaultLanguage();
        if (language != null) return language;
        
        // (4)
        return DEFAULT_LANGUAGE;
    }

    /**
     * Get property value from resource configuration
     */
    public String getResourceConfigProperty(String name) throws Exception {
        ResourceConfiguration rc = getConfiguration();
        if (rc != null) return rc.getProperty(name);
        return getRTI().getProperty(name);
    }
    
    /**
     * Get property value from resource configuration
     */
    public String[] getResourceConfigProperties(String name) throws Exception {
        ResourceConfiguration rc = getConfiguration();
        if (rc != null) return rc.getProperties(name);
        return getRTI().getProperties(name);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
        this.request = environment.getRequest();
        this.response = environment.getResponse();
    }


}
