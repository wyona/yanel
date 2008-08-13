/*
 * Copyright 2007 Wyona
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

package org.wyona.yanel.impl.resources.redirect;

import org.w3c.dom.Document;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.Yanel;

import org.wyona.yanel.core.api.attributes.CreatableV2;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.wyona.yanel.core.map.Realm;

import org.wyona.security.core.api.Identity;

import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationUtil;

import org.apache.log4j.Category;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.serializer.Serializer;

/**
 *
 */
public class RedirectResource extends Resource implements ViewableV2, CreatableV2 {

    private static Category log = Category.getInstance(RedirectResource.class);
    
    public static String IDENTITY_MAP_KEY = "identity-map";

    // Only a temporary variable needed during creation (roundtrip)
    private String defaultHrefSetByCreator;
    private static String REDIRECT_URL = "redirectURL";
    
    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        ViewDescriptor[] vd = new ViewDescriptor[1];
        vd[0] = new ViewDescriptor("default");
        vd[0].setMimeType(null);
        return vd;
    }

    /**
     *
     */
    public View getView(String viewId) throws Exception {
        return getView(viewId, null);
    }

    /**
     * Generates view
     */
    public View getView(String viewId, String revisionName) throws Exception {
        View view = new View();
        view.setResponse(false); // this resource writes the response itself

        HttpServletResponse response = getResponse();

        String defaultHref = getResourceConfigProperty("href");

        if (defaultHref == null) throw new Exception("No default redirect has been set!");

        // Default
        response.setStatus(307);
        response.setHeader("Location", defaultHref);

        ResourceConfiguration rc = getConfiguration();
        Document customConfigDoc = rc.getCustomConfiguration();
        if (customConfigDoc != null) {
            Configuration config = ConfigurationUtil.toConfiguration(customConfigDoc.getDocumentElement());

            // Localization
            Configuration[] languageRedirectConfigs = config.getChildren("language");

            // NOTE: Within realm.xml one can overwrite the language handler, for example using the content language instead, which is overwriting getRequestedLanguage(), but this doesn't make sense in the case of the redirect resource (also see http://lists.wyona.org/pipermail/yanel-development/2008-April/002150.html)
            String localizationLanguage = new org.wyona.yanel.impl.DefaultLanguageHandler().getLocalizationLanguage(this);
            //String localizationLanguage = getRequestedLanguage();
            log.debug("Localization: " + localizationLanguage);
            for (int i = 0; i < languageRedirectConfigs.length; i++) {
                try {
                    if (languageRedirectConfigs[i].getAttribute("code").equals(localizationLanguage)) {
                        response.setStatus(307);
                        response.setHeader("Location", languageRedirectConfigs[i].getAttribute("href"));
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw e;
                }
            }
        

            // Username
            String currentUser = null;
            Identity identity = getIdentity(getRequest());
            if (identity != null) {
                currentUser = identity.getUsername();
            }
            if (currentUser != null) {
                Configuration[] userRedirectConfigs = config.getChildren("user");
                for (int i = 0; i < userRedirectConfigs.length; i++) {
                    try {
                        if (userRedirectConfigs[i].getAttribute("name") == currentUser || (currentUser).equals(userRedirectConfigs[i].getAttribute("name"))) {
                            response.setStatus(307);
                            response.setHeader("Location", userRedirectConfigs[i].getAttribute("href"));
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        throw e;
                    }
                }
            }
        }
        return view;
    }
    
    /**
     *
     */
    public String getMimeType(String viewid) {
        return null;
    }
    
    /**
     * Always return true, because if a resource config is set, then it also exists. TODO: One might want to consider a more sophisticated exists() ...
     */
    public boolean exists() throws Exception {
        return true; 
    }
    
    /**
     * 
     */
    public long getSize() throws Exception {
        return -1;
    }

    /**
     * Gets the identity from the session associated with the given request.
     * @param request
     * @return identity or null if there is no identity in the session for the current
     *                  realm or if there is no session at all
     */
    private Identity getIdentity(HttpServletRequest request) throws Exception {
        return getEnvironment().getIdentity();
    }

    // All methods below are re CreatableV2 and CreatableV1 interface implementations

    /**
     *
     */
    public void create(HttpServletRequest request) {
        log.warn("Do nothing! Only resource configuration is needed.");
    }

    /**
     *
     */
    public String getPropertyType(String propertyName) {
        return CreatableV2.TYPE_STRING;
    }

    /**
     *
     */
    public String getCreateName(String suggestedName) {
        return suggestedName;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.CreatableV2#createRTIProperties(HttpServletRequest)
     */
    public HashMap createRTIProperties(HttpServletRequest request) {
        HashMap map = new HashMap();
        map.put("href", request.getParameter("rp." + REDIRECT_URL));
        return map;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.CreatableV1#getProperty(String)
     */
    public Object getProperty(String name) {
        log.error("DEBUG: name: " + name);
        return defaultHrefSetByCreator;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.CreatableV1#setProperty(String, Object)
     */
    public void setProperty(String name, Object value) {
        log.error("DEBUG: name: " + name);
        defaultHrefSetByCreator = (String) value;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.CreatableV1#getPropertyNames()
     */
    public String[] getPropertyNames() {
        String[] pn = new String[1];
        pn[0] = REDIRECT_URL;
        return pn;
    }
}
