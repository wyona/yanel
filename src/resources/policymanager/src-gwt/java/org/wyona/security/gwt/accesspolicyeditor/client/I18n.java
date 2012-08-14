/*
 * Copyright 2008 Wyona
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.wyona.security.gwt.accesspolicyeditor.client;

/**
 * Also see http://code.google.com/webtoolkit/tutorials/1.6/i18n.html
 */
public class I18n {

    /**
     *
     */
    public static String getLabel(String key, String language) {
        if (language.equals("en")) {
            if (key.equals("search-box-label")) {
                return "Search users and groups: ";
            } else if(key.equals("inherit-rights-label")) {
                return "Inherit rights from parent policies";
            } else if(key.equals("list-box-identities")) {
                return "Identities (Users and Groups)";
            } else if(key.equals("button-all")) {
                return "All";
            }
        } else if (language.equals("de")) {
            if (key.equals("search-box-label")) {
                return "Suche Benutzer/Gruppen: ";
            } else if(key.equals("inherit-rights-label")) {
                return "Uebernehme vererbte Rechte der Eltern-Policies";
            } else if(key.equals("list-box-identities")) {
                return "Identitäten (Benutzer und Gruppen)";
            } else if(key.equals("button-all")) {
                return "Alle";
            }
        } else {
            return "WARNING: No such language '" + language + "' implemented!";
        }
        return "WARNING: No such key '" + key + "' and language '" + language + "' implemented!";
    }
}
