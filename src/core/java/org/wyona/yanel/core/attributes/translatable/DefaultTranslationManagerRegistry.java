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

import java.util.HashMap;

import org.apache.log4j.Category;
import org.wyona.yanel.core.map.Realm;

/**
 *
 */
public class DefaultTranslationManagerRegistry implements TranslationManagerRegistry {

    private static Category log = Category.getInstance(DefaultTranslationManagerRegistry.class);
    
    private HashMap translationManagers; // realm id is the key
    
    /**
     *
     */
    public DefaultTranslationManagerRegistry() {
        this.translationManagers = new HashMap();
    }
    
    public TranslationManager getTranslationManager(Realm realm) throws TranslationException {
        if (this.translationManagers.containsKey(realm.getID())) {
            return (TranslationManager)this.translationManagers.get(realm.getID());
        }
        TranslationManager manager = new DefaultTranslationManager(realm);
        this.translationManagers.put(realm.getID(), manager);
        return manager;
    }
}
