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

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.map.Realm;

/**
 * This is a delegate for the translatable interface.
 * @see org.wyona.yanel.core.api.attributes.TranslatableV1
 */
public interface TranslationManager {
    
    void init(Realm realm) throws TranslationException;
    
    String getLanguage(Resource resource) throws TranslationException;
    
    String[] getLanguages(Resource resource) throws TranslationException;
    
    Resource getTranslation(Resource resource, String language) throws TranslationException;

    /**
     * @param resource Existing resource
     * @param newResource New resource which shall be translation of existing resource
     * @param language Language of new translation
     */
    void addTranslation(Resource resource, Resource newResource, String language) throws TranslationException;
    
    void removeTranslation(Resource resource, String language) throws TranslationException;
    
    boolean hasTranslation(Resource resource, String language) throws TranslationException;

}
