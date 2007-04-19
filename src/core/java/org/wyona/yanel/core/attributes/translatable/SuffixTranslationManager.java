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
 * This translation manager works with a path format where the language
 * is a suffix: /foo/bar_en.html
 */
public class SuffixTranslationManager extends AbstractPathTranslationManager {

    private static Category log = Category.getInstance(SuffixTranslationManager.class);
    
    /**
     * Empty constructor. Don't forget to call the init() method.
     */
    public SuffixTranslationManager() {
    }
    
    /**
     * Extracts the language from the path, if possible.
     * This implementation expects the following format: /foo/bar_en.html
     * @param path
     * @return language or null if the path does not contain 
     */
    protected String getLanguageFromPath(String path) {
        int dotIndex = path.lastIndexOf('.');
        if (dotIndex > 0) {
            path = path.substring(0, dotIndex);
        }
        int underscoreIndex = path.lastIndexOf('_');
        String suffix = null;
        if (underscoreIndex > -1) {
            suffix = path.substring(underscoreIndex + 1);
        }
        if (suffix != null && suffix.length() == 2) {
            return suffix;
        } else {
            return null;
        }
    }
    
    /**
     * Gets the path of an alternate language version by substituting the language
     * in the given path.
     * This implementation expects the following path format: /foo/bar_en.html
     * @param path
     * @param language
     * @return path for given language or null if the given path contains no language.
     */
    protected String getPath(String path, String language) {
        int dotIndex = path.lastIndexOf('.');
        String extension = "";
        if (dotIndex > 0) {
            extension = path.substring(dotIndex + 1);
            path = path.substring(0, dotIndex);
        }
        int underscoreIndex = path.lastIndexOf('_');
        String basePath = null;
        if (underscoreIndex > -1) {
            basePath = path.substring(0, underscoreIndex);
        }
        if (basePath != null && basePath.length() > 0) {
            return basePath + "_" + language + "." + extension;
        } else {
            return null;
        }
    }
    
}
