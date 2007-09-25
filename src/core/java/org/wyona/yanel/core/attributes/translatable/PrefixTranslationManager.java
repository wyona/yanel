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

package org.wyona.yanel.core.attributes.translatable;

import org.apache.log4j.Category;

/**
 * This translation manager works with a path format where the language is
 * a prefix: /en/foo/bar.html
 */
public class PrefixTranslationManager extends AbstractPathTranslationManager {

    private static Category log = Category.getInstance(PrefixTranslationManager.class);
    
    /**
     * Empty constructor. Don't forget to call the init() method.
     */
    public PrefixTranslationManager() {
    }
    
    /**
     * Extracts the language from the path, if possible.
     * This implementation expects the following format: /en/foo/bar.html
     * @param path
     * @return language or null if the path does not contain 
     */
    protected String getLanguageFromPath(String path) {
        String[] tokens = path.split("/");
        if (tokens.length > 1 && tokens[1].length() == 2) {
            // TODO: should check if it's a valid language?
            return tokens[1];
        }
        return null;
    }
    
    /**
     * Gets the path of an alternate language version by substituting the language
     * in the given path.
     * This implementation expects the following path format: /en/foo/bar.html
     * @param path
     * @param language
     * @return path for given language or null if the given path contains no language.
     */
    protected String getPath(String path, String language) {
        if (path.startsWith("/")) {
            path = path.substring(1);
            int slashIndex = path.indexOf('/');
            if (slashIndex == 2) {
                return "/" + language + path.substring(slashIndex);
            }
        }
        return null;
    }
    
}
