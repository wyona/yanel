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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Category;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceManager;
import org.wyona.yanel.core.Yanel;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.RepositoryException;
import org.apache.commons.id.uuid.UUID;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 */
public class DefaultTranslationManager implements TranslationManager {

    private static Category log = Category.getInstance(DefaultTranslationManager.class);
    
    private HashMap languageVersions; // key is path
    private HashSet pages;
    private Node node;
    
    /**
     * Empty constructor. Don't forget to call the init() method.
     */
    public DefaultTranslationManager() {
    }
   
    /**
     *
     */
    public DefaultTranslationManager(Realm realm) throws TranslationException {
        init(realm);
    }
    
    public void init(Realm realm) throws TranslationException {
        try {
            this.pages = new HashSet();
            this.languageVersions = new HashMap();
            String translationsPath = "/translations.xml";
            if (realm.getRepository().existsNode(translationsPath)) {
                this.node = realm.getRepository().getNode(translationsPath);
                load();
            } else {
                log.warn("Realm " + realm.getID() + " contains no translations.xml");
            }
        } catch (Exception e) {
            throw new TranslationException(e.getMessage(), e);
        }
        
    }

    public synchronized String getLanguage(Resource resource) throws TranslationException {
        LanguageVersion langVersion = getLanguageVersion(resource.getPath());
        if (langVersion != null) {
            return langVersion.language;
        }
        return null;
    }
    
    public synchronized String[] getLanguages(Resource resource) throws TranslationException {
        Page langSet = getPage(resource);
        if (langSet == null) {
            return new String[0];
        }
        String[] languages = new String[langSet.size()];
        languages = (String[])langSet.keySet().toArray(languages);
        return languages;
    }
    
    public synchronized Resource getTranslation(Resource resource, String language) throws TranslationException {
        Page langSet = getPage(resource);
        if (langSet != null && langSet.containsKey(language)) {
            LanguageVersion target = (LanguageVersion)langSet.get(language);
            ResourceManager resourceManager;
            try {
                resourceManager = Yanel.getInstance().getResourceManager();
                return resourceManager.getResource(resource.getRequest(), resource.getResponse(), resource.getRealm(), target.path);
            } catch (Exception e) {
                throw new TranslationException(e.getMessage(), e);
            }
        }
        return null;
    }

    public synchronized void addTranslation(Resource resource, Resource newResource, String language) throws TranslationException {
        Page langSet = getPage(resource);
        
        if (langSet == null) {
            langSet = new Page();
            this.pages.add(langSet);
        }
        
        LanguageVersion newLangVersion = new LanguageVersion(newResource.getPath(), "", language, langSet);
        
        this.languageVersions.put(newResource.getPath(), newLangVersion);
        langSet.put(language, newLangVersion);
        
        save();
    }
    
    public synchronized void removeTranslation(Resource resource, String language) throws TranslationException {
        Page langSet = getPage(resource);
        langSet.remove(language);
        
        this.languageVersions.remove(resource.getPath());
        
        if (langSet.isEmpty()) {
            this.pages.remove(langSet);
        }
        
        save();
    }
    
    public synchronized boolean hasTranslation(Resource resource, String language) throws TranslationException {
        Page langSet = getPage(resource);
        if (langSet == null) {
            return false;
        }
        return langSet.containsKey(language);
    }
    
    
    
    private synchronized LanguageVersion getLanguageVersion(String path) throws TranslationException {
        return (LanguageVersion)this.languageVersions.get(path);
    }

    private synchronized Page getPage(Resource resource) throws TranslationException {
        LanguageVersion langVersion = getLanguageVersion(resource.getPath());
        if (langVersion == null) {
            return null;
        }
        return langVersion.page;
    }
    
    private synchronized void load() throws TranslationException {
        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            InputStream is = this.node.getInputStream();
            saxParser.parse(is, new TranslationSAXHandler());
            //debug();
        } catch (Exception e) {
            log.error(e, e);
            throw new TranslationException(e.getMessage(), e);
        }
    }
    
    private synchronized void save() throws TranslationException {
        try {
            OutputStream os = this.node.getOutputStream();
            Writer out = new OutputStreamWriter(os);
            out.write("<?xml version='1.0'?>\n");
            out.write("<pages>\n");
            Iterator pageIter = this.pages.iterator();
            while (pageIter.hasNext()) {
                out.write("<page>\n");
                Page page = (Page)pageIter.next();
                Iterator iter = page.keySet().iterator();
                while (iter.hasNext()) {
                    String key = (String)iter.next();
                    LanguageVersion langVersion = (LanguageVersion) page.get(key);
                    out.write("<translation path=\"" + langVersion.path + "\" uuid=\"\" lang=\"" + 
                            langVersion.language + "\"/>\n");
                }
                out.write("</page>\n");
            }
            out.write("</pages>\n");
            out.close();
        } catch (Exception e) {
            log.error(e, e);
            throw new TranslationException(e.getMessage(), e);
        }
    }
    
    private synchronized void debug() throws TranslationException {
        try {
            OutputStream os = System.out;
            Writer out = new OutputStreamWriter(os);
            out.write("<?xml version='1.0'?>\n");
            out.write("<pages>\n");
            Iterator pageIter = this.pages.iterator();
            while (pageIter.hasNext()) {
                out.write("<page>\n");
                Page page = (Page)pageIter.next();
                Iterator iter = page.keySet().iterator();
                while (iter.hasNext()) {
                    String key = (String)iter.next();
                    LanguageVersion langVersion = (LanguageVersion) page.get(key);
                    out.write("<translation path=\"" + langVersion.path + "\" uuid=\"\" lang=\"" + 
                            langVersion.language + "\"/>\n");
                }
                out.write("</page>\n");
            }
            out.write("</pages>\n");
            out.close();
        } catch (Exception e) {
            log.error(e, e);
            throw new TranslationException(e.getMessage(), e);
        }
    }
    
    private synchronized Page registerTranslation(String path, String uuid, String language, Page page) {
        if (page == null) {
            page = new Page();
            this.pages.add(page);
        }
        LanguageVersion langVersion = new LanguageVersion(path, uuid, language, page);
        page.put(language, langVersion);
        this.languageVersions.put(path, langVersion);
        return page;
    }

    private class LanguageVersion {
        public LanguageVersion(String path, String uuid, String language, Page page) {
            this.path = path;
            this.uuid = uuid;
            this.language = language;
            this.page = page;
        }
        public String path;
        public String uuid;
        public String language;
        public Page page;  
    }
    
    /**
     * A page is a set of language versions.
     */
    private class Page extends HashMap {
        // key is language
    }
    

    /**
     * Fills the hashmap with the data provided by the SAX stream.
     */
    public class TranslationSAXHandler extends DefaultHandler {
        
        private Page page;
        /**
         * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        public void startElement(String uri, String loc, String raw, Attributes attr) 
        throws SAXException {
            String name = loc;
            if (name == null || name.length()==0) {
                name = raw;
            }
            if (name.equals("page")) {
                this.page = null;
            } else if (name.equals("translation")) {
                int pathIndex = attr.getIndex("path");
                int uuidIndex = attr.getIndex("uuid");
                int langIndex = attr.getIndex("language");
                String path = attr.getValue(pathIndex);
                String uuid = attr.getValue(uuidIndex);
                String lang = attr.getValue(langIndex);

                this.page = DefaultTranslationManager.this.registerTranslation(path, uuid, lang, this.page);
            }
        }
    }
}
