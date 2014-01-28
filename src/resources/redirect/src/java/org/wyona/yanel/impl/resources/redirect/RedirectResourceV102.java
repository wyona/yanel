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

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.api.attributes.CreatableV2;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.servlet.AccessLog;

import org.wyona.security.core.api.Identity;
import java.lang.Integer;
import java.util.HashMap;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationUtil;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.wyona.boost.client.BoostService;
import com.wyona.boost.client.ServiceException;
import com.wyona.boost.client.BoostServiceConfig;
import com.wyona.boost.client.HistoryEntry;

/**
 * Controller to do redirects (supports personalized redirect)
 */
public class RedirectResourceV102 extends Resource implements ViewableV2, CreatableV2 {

    private static final Logger log = LogManager.getLogger(RedirectResourceV102.class);
    
    public static String IDENTITY_MAP_KEY = "identity-map";
    private int TMP_REDIRECT_STATUS_CODE = 307;
    private String LOCATION = "Location";

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
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getView(String)
     */
    public View getView(String viewId) throws Exception {
        View view = new View();
        view.setResponse(false); // this resource writes the response itself

        HttpServletResponse response = getEnvironment().getResponse();

        response.setStatus(TMP_REDIRECT_STATUS_CODE);
        String location = getLocation();
        if (location.indexOf("/") == 0) { // INFO: Redirect is absolute path
            log.debug("Current path: " + getPath() + ", Back 2 realm: " + org.wyona.yanel.core.util.PathUtil.backToRealm(getPath()) + ", Absolute redirect path: " + location);
            location = org.wyona.yanel.core.util.PathUtil.backToRealm(getPath()) + location.substring(1);
        }
        //log.debug("Location: " + location);
        response.setHeader(LOCATION, location);

        return view;
    }

    /**
     * Get location from resource configuration
     * @return redirect URL
     */
    private String getLocation() throws Exception {

        String defaultHref = getResourceConfigProperty("href");
        if (defaultHref == null) {
            throw new Exception("No default redirect has been set inside resource configuration!");
        } else {
            log.debug("Default href: " + defaultHref);
        }

        String location = defaultHref;

        // Username
        String currentUser = null;
        Identity identity = getIdentity(getRequest());
        if (identity != null) {
            currentUser = identity.getUsername();
        }
        boolean isLoggedIn = currentUser != null;

        ResourceConfiguration rc = getConfiguration();
        Document customConfigDoc = rc.getCustomConfiguration();
        if (customConfigDoc != null) {
            Configuration config = ConfigurationUtil.toConfiguration(customConfigDoc.getDocumentElement());

            // INFO: Personalization
            Configuration[] personalizedRedirectConfigs = config.getChildren("personalized");
            if (personalizedRedirectConfigs != null && personalizedRedirectConfigs.length > 0) {
                String serviceUrl = personalizedRedirectConfigs[0].getAttribute("boost-service-url");
                String apiKey = personalizedRedirectConfigs[0].getAttribute("boost-api-key");
                String personalizedHref = personalizedRedirectConfigs[0].getAttribute("href");
                //log.debug("Personalization of redirect is configured: " + serviceUrl + ", " + apiKey);
                Cookie cookie = AccessLog.getYanelAnalyticsCookie(getEnvironment().getRequest());
                String cookieVal = cookie.getValue();
                try {
                    Iterable<HistoryEntry> clickStream = getClickstream(serviceUrl, cookieVal, getRealm().getUserTrackingDomain(), apiKey);
                    String clickstreamLang = getLanguage(clickStream);
                    if (clickstreamLang != null) {
                        log.warn("DEBUG: User language from personal click stream: " + clickstreamLang);
                        location =personalizedHref.replace("@LANG", clickstreamLang);
                        return location;
                    } else {
                        log.warn("Not able to detect user language from click stream.");
                    }
                } catch(Exception e) {
                    log.error(e, e);
                    log.warn("Fallback to non-personalized redirect...");
                }
            } else {
                log.debug("Personalization of redirect not configured.");
            }

            // Localization
            Configuration[] languageRedirectConfigs = config.getChildren("language");

            // NOTE: Within realm.xml one can overwrite the language handler, for example using the content language instead, which is overwriting getRequestedLanguage(), but this doesn't make sense in the case of the redirect resource (also see http://lists.wyona.org/pipermail/yanel-development/2008-April/002150.html)
            String localizationLanguage = new org.wyona.yanel.impl.DefaultLanguageHandler().getLocalizationLanguage(this);
            //String localizationLanguage = getRequestedLanguage();
            log.debug("Localization: " + localizationLanguage);
            for (int i = 0; i < languageRedirectConfigs.length; i++) {
                try {
                    String lang = languageRedirectConfigs[i].getAttribute("code");
                    if (lang.equals(localizationLanguage) || lang.equals("*")) {
                        String href = languageRedirectConfigs[i].getAttribute("href");
                        location = href;

                        String if_logged_in = languageRedirectConfigs[i].getAttribute("if-logged-in", "false");
                        if("true".equals(if_logged_in) && !isLoggedIn) {
                            continue;
                        }

                        String device = languageRedirectConfigs[i].getAttribute("device", null);
                        if (device != null) {
                            if (device.equals("web.xml:mobile-devices")) {
                                //log.debug("Client language '" + localizationLanguage + "' matched and device '" + device + "' is supported. Let's check whether client is a mobile device ...");
                                if (isMobileDevice()) {
                                    //log.debug("Client is mobile device, hence redirect to: " + href);
                                    return location;
                                } else {
                                    //log.debug("Client is not a mobile device.");
                                    continue;
                                }
                            } else {
                                log.warn("Device configuration '" + device + "' not supported yet.");
                                continue;
                            }
                        } else {
                            //log.debug("No device specified (Language: " + localizationLanguage + ").");
                        }
                        return location;
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw e;
                }
            }
        
            if (currentUser != null) {
                Configuration[] userRedirectConfigs = config.getChildren("user");
                for (int i = 0; i < userRedirectConfigs.length; i++) {
                    try {
                        if (userRedirectConfigs[i].getAttribute("name") == currentUser || (currentUser).equals(userRedirectConfigs[i].getAttribute("name"))) {
                            location = userRedirectConfigs[i].getAttribute("href");
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        throw e;
                    }
                }
            }
        }
        return location;
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
        log.debug("name: " + name);
        return defaultHrefSetByCreator;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.CreatableV1#setProperty(String, Object)
     */
    public void setProperty(String name, Object value) {
        log.debug("name: " + name);
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

    /**
     * Check whether user agent is a mobile device
     */
    private boolean isMobileDevice() {
        javax.servlet.http.HttpSession session = getEnvironment().getRequest().getSession(true);
        if (session != null) {
            String mobileDevice = (String) session.getAttribute("yanel.mobile");
            //TODO: String mobileDevice = (String) getEnvironment().getRequest().getSession(true).getAttribute(org.wyona.yanel.servlet.YanelServlet.MOBILE_KEY);
            if (mobileDevice != null && !mobileDevice.equals("false")) {
                return true;
            } else {
                return false;
            }
        } else {
            log.warn("No HTTP session available (maybe because Yanel is used via the command line or some custom junit tests do not provide session handling)!");
            return false;
        }
    }

    /**
     * Get clickstream for a given cookie (also see http://en.wikipedia.org/wiki/Clickstream)
     * @param boostServiceUrl Boost service URL
     * @param cookie Unique cookie id
     * @param realm Domain name
     * @param apiKey Key to access Boost API
     * @return list of URLs which were requested by user with a given cookie
     */
    private Iterable<HistoryEntry> getClickstream(String boostServiceUrl, String cookie, String realm, String apiKey) throws Exception {
        BoostServiceConfig bsc = new BoostServiceConfig(boostServiceUrl, realm, apiKey);
        BoostService boost = new BoostService(bsc);
        return boost.getClickStream(cookie);
    }

    /**
     * Get user language from clickstream
     * @param clickStream URLs which have been requested by user
     * @return language with highest count
     */
    private String getLanguage(Iterable<HistoryEntry> clickStream) {
        HashMap usedLangs = new HashMap();
        for(HistoryEntry he : clickStream) {
            //log.debug("Previous request: " + new java.util.Date(he.getTime()).toString() + ", " + he.getURL());
            String language = getLanguage(he.getURL());
            if (language != null) {
                if (usedLangs.containsKey(language)) {
                    int count = ((Integer) usedLangs.get(language)).intValue();
                    count++; // WARN: If we increase the count as an argument of the Integer below, then it somehow does not get increased!
                    usedLangs.put(language, new Integer(count));
                } else {
                    //log.warn("DEBUG: Add language '" + language + "' to has map.");
                    usedLangs.put(language, new Integer(1));
                }
            } else {
                //log.warn("No language detected for requested URL '" + he.getURL() + "'!");
            }
        }
 
        int highestCount = 0;
        String langWithHighestCount = null;
        java.util.Iterator it = usedLangs.entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry kv = (java.util.Map.Entry) it.next();
            int count = ((Integer) kv.getValue()).intValue();
            //log.debug("Key/value: " + kv.getKey() + ":" + kv.getValue());
            if (count > highestCount) {
                highestCount = count;
                langWithHighestCount = (String) kv.getKey();
            }
        }
        if (langWithHighestCount != null) {
            return langWithHighestCount;
        }
        return null;
    }

    /**
     * Get requested language
     * @param url Requested URL, which might contain requested language either as prefix or suffix
     */
    private String getLanguage(String url) {
        try {
            String path = new java.net.URL(url).getPath();
            if (path.length() >= 17 && path.charAt(13) == '/' && path.charAt(16) == '/') {
                String lang = path.substring(14,16);
                //log.debug("Language: " + lang + " (Path: " + path + ")");
                return lang;
            } else {
                //log.debug("No language detected for path: " + path);
            }
        } catch(Exception e) {
            log.error(e, e);
        }
        return null;
    }
}
