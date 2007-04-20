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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Category;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceManager;
import org.wyona.yanel.core.Yanel;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.util.ResourceAttributeHelper;

/**
 * This translation manager is an extension of the DefaultTranslationManager.
 * In addition to the translations.xml it considers the path of the resources.
 * If a resource is not registered in translations.xml, it will try to
 * deduce the language from the path, whereas a subclass must implement the
 * methods corresponding to their path format. 
 */
public abstract class AbstractPathTranslationManager extends DefaultTranslationManager {

    private static Category log = Category.getInstance(AbstractPathTranslationManager.class);
    
    protected ResourceManager resourceManager;
    
    /**
     * Empty constructor. Don't forget to call the init() method.
     */
    public AbstractPathTranslationManager() {
    }
    
    protected ResourceManager getResourceManager() {
        if (this.resourceManager == null) {
            try {
                this.resourceManager = Yanel.getInstance().getResourceManager();
            } catch (Exception e) {
                log.error(e, e);
            }
        }
        return this.resourceManager;
    }
   
    public synchronized String getLanguage(Resource resource) throws TranslationException {
        LanguageVersion langVersion = getLanguageVersion(resource.getPath());
        if (langVersion != null) {
            return langVersion.language;
        }
        return getLanguageFromPath(resource.getPath());
    }
    
    /**
     * Extracts the language from the path, if possible.
     * @param path
     * @return language or null if the path does not contain 
     */
    protected abstract String getLanguageFromPath(String path);
    
    /**
     * Gets the path of an alternate language version by substituting the language
     * in the given path.
     * @param path
     * @param language
     * @return path for given language or null if the given path contains no language.
     */
    protected abstract String getPath(String path, String language);
    
    /**
     * Gets a alternative language version of the given resource by replacing the 
     * language in the path of the given resource.
     * @param resource
     * @param language
     * @return resource with given language or null if resource does not exist or 
     *                  if resource path does not contain a language.
     * @throws Exception
     */
    protected Resource getResource(Resource resource, String language) throws Exception {
        String possiblePath = getPath(resource.getPath(), language);
        if (possiblePath != null) {
            Resource possibleResource = getResourceManager().getResource(resource.getRequest(), resource.getResponse(), resource.getRealm(), possiblePath);
            if (ResourceAttributeHelper.hasAttributeImplemented(possibleResource, "Viewable", "2")) {
                ViewableV2 viewable = (ViewableV2)possibleResource;
                if (viewable.exists()) {
                    return possibleResource;
                }
            }
        }
        return null;
    }
    
    public synchronized String[] getLanguages(Resource resource) throws TranslationException {
        Page page = getPage(resource);
        if (page == null) {
            try {
                String[] realmLanguages = resource.getRealm().getLanguages();
                ArrayList existingLanguages = new ArrayList();
                for (int i = 0; i < realmLanguages.length; i++) {
                    String possibleLanguage = realmLanguages[i];
                    Resource possibleResource = getResource(resource, possibleLanguage);
                    if (possibleResource != null) {
                        existingLanguages.add(possibleLanguage);
                    }
                }
                return (String[])existingLanguages.toArray(new String[existingLanguages.size()]);
            } catch (Exception e) {
                log.error(e, e);
                throw new TranslationException(e.getMessage(), e);
            }
        }
        String[] languages = new String[page.size()];
        // return languages in the order of the realm languages:
        Set existingLanguages = page.keySet();
        String[] realmLanguages = resource.getRealm().getLanguages();
        int j = 0;
        for (int i = 0; i < realmLanguages.length; i++) {
            if (existingLanguages.contains(realmLanguages[i])) {
                languages[j++] = realmLanguages[i];
            }
        }
        return languages;
    }
    
    public synchronized Resource getTranslation(Resource resource, String language) throws TranslationException {
        Page page = getPage(resource);
        if (page != null && page.containsKey(language)) {
            LanguageVersion target = (LanguageVersion)page.get(language);
            try {
                return getResourceManager().getResource(resource.getRequest(), resource.getResponse(), resource.getRealm(), target.path);
            } catch (Exception e) {
                throw new TranslationException(e.getMessage(), e);
            }
        } else if (page == null) {
            try {
                return getResource(resource, language);
            } catch (Exception e) {
                log.error(e, e);
                throw new TranslationException(e.getMessage(), e);
            }
        }
        return null;
    }

    public synchronized void addTranslation(Resource resource, Resource newResource, String language) throws TranslationException {
        Page page = getPage(resource);
        
        if (page == null) {
            page = new Page();
            this.pages.add(page);
            
            // add existing language versions first:
            String[] languages = getLanguages(resource);
            for (int i = 0; i < languages.length; i++) {
                String path = getPath(resource.getPath(), languages[i]);
                if (path != null) {
                    LanguageVersion newLangVersion = new LanguageVersion(path, "", languages[i], page);
                    this.languageVersions.put(path, newLangVersion);
                    page.put(languages[i], newLangVersion);
                }
            }
        }
        
        LanguageVersion newLangVersion = new LanguageVersion(newResource.getPath(), "", language, page);
        this.languageVersions.put(newResource.getPath(), newLangVersion);
        page.put(language, newLangVersion);
        
        save();
    }
    
    public synchronized void removeTranslation(Resource resource, String language) throws TranslationException {
        Page page = getPage(resource);
        page.remove(language);
        
        this.languageVersions.remove(resource.getPath());
        
        if (page.isEmpty()) {
            this.pages.remove(page);
        }
        
        save();
    }
    
    public synchronized boolean hasTranslation(Resource resource, String language) throws TranslationException {
        Page page = getPage(resource);
        if (page == null) {
            try {
                return getResource(resource, language) != null;
            } catch (Exception e) {
                log.error(e, e);
                throw new TranslationException(e.getMessage(), e);
            }
        }
        return page.containsKey(language);
    }
    
}
