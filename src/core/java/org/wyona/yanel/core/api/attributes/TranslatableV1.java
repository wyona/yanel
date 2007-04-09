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

package org.wyona.yanel.core.api.attributes;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.attributes.translatable.TranslationException;

/**
 * Interface for multi-lingual resources which provide different
 * translations (aka language versions).
 * 
 * DEV (not released yet, this interface still might change ...)
 */
public interface TranslatableV1 {
    /**
     * Gets the language of this resource.
     * @return language
     */
    String getLanguage() throws TranslationException;

    /**
     * Gets all existing languages of this resource.
     * @return array of languages
     */
    String[] getLanguages() throws TranslationException;

    /**
     * Gets a specific translation of this resource. 
     * @param language
     * @return translation or null if no translation for this language exists.
     */
    Resource getTranslation(String language) throws TranslationException;

    /**
     * Adds a translation to this resource.
     * If a translation for the given language already exists, it will be replaced.
     * @param resource translation
     * @param language
     */
    void addTranslation(Resource resource, String language) throws TranslationException;

    /**
     * Removes a translation from this resource.
     * @param language
     */
    void removeTranslation(String language) throws TranslationException;

    /**
     * Indicates whether this resource has a translation for the given language.
     * @param language
     * @return true if translation for this language exists.
     */
    boolean hasTranslation(String language) throws TranslationException;
}