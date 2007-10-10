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

package org.wyona.yanel.impl;

import javax.servlet.http.HttpServletRequest;

import org.wyona.yanel.core.LanguageHandler;
import org.wyona.yanel.core.Resource;

/**
 *
 */
public class DefaultLanguageHandler implements LanguageHandler {

    /**
     * Get language (localization) with the following priorization: <br><br>
     * 1) yanel.meta.language query string parameter<br> 
     * 2) Accept-Language header<br>
     * 3) Realm default language<br>
     * 4) Default "en"<br>
     */
    public String getLocalizationLanguage(Resource resource) throws Exception {
        HttpServletRequest request = resource.getEnvironment().getRequest();
        
        // TODO: Make this reusable. Also see org/wyona/yanel/servlet/YanelServlet.java

        // TODO: Use user profile setting resp. session (allow switching the locale)
        
        // (1)
        String language = request.getParameter("yanel.meta.language");
        if (language != null) return language;
        
        /*String cookieName = "yanel." + getRealm().getID() + ".language";
        Cookie[] cookies = getRequest().getCookies();
        for (int i = 0 ; i < cookies.length; i++) {
            if (cookies[i].getName().equals(cookieName)) {
                language = cookies[i].getValue();
                log.debug("language-cookie is: " + language);
            }
        }
        if (language != null) return language;
        */
        
        // (2)
        language = request.getHeader("Accept-Language");
        if (language != null) {
            if (language.indexOf(",") > 0) {
                language = language.substring(0, language.indexOf(","));
            }
            int dashIndex = language.indexOf("-");
            if (dashIndex > 0) {
                language = language.substring(0, dashIndex);
            }
            
            // only accept language if it is supported by the realm:
            /*List realmLanguages = Arrays.asList(getRealm().getLanguages());
            if (!realmLanguages.contains(language)) {
                language = getRealm().getDefaultLanguage();
            }*/
            return language;
        }

        // (3)
        language = resource.getRealm().getDefaultLanguage();
        if (language != null) return language;
        
        // (4)
        return Resource.DEFAULT_LANGUAGE;
    }

}
