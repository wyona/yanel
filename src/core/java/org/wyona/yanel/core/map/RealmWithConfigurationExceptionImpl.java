/*
 * Copyright 2009 Wyona
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

package org.wyona.yanel.core.map;

import java.io.File;
import java.util.ArrayList;

import org.wyona.commons.io.FileUtil;
import org.wyona.security.core.IdentityManagerFactory;
import org.wyona.security.core.PolicyManagerFactory;
import org.wyona.security.core.api.IdentityManager;
import org.wyona.security.core.api.PolicyManager;
import org.wyona.yanel.core.LanguageHandler;
import org.wyona.yanel.core.Yanel;
import org.wyona.yanel.core.attributes.translatable.DefaultTranslationManager;
import org.wyona.yanel.core.attributes.translatable.TranslationManager;
import org.wyona.yanel.core.api.security.WebAuthenticator;
import org.wyona.yanel.core.navigation.Sitetree;
import org.wyona.yanel.core.util.ConfigurationUtil;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.xml.sax.SAXException;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

import org.apache.log4j.Logger;

/**
 * Realm with configuration exception (for example during startup or reload)
 */
public class RealmWithConfigurationExceptionImpl implements Realm {

    public static String DEFAULT_REPOSITORY_FACTORY_BEAN_ID = "DefaultRepositoryFactory";
    private static String EXTRA_REPOSITORY_FACTORY_BEAN_ID = "ExtraRepositoryFactory";

    private static Logger log = Logger.getLogger(RealmWithConfigurationExceptionImpl.class);

    private String name;
    private String id;
    private String mountPoint;
    private File configFile;
    private Exception configurationException;



    private String defaultLanguage;
    private Repository repository;
    private Repository rtiRepository;
    private PolicyManager privatePolicyManager;
    private IdentityManager privateIdentityManager;
    private WebAuthenticator privateWebAuthenticator;
    private TranslationManager translationManager;
    private LanguageHandler languageHandler;
    private Sitetree repoNavigation;
    private File rootDir;
    private String[] languages;
    private String i18nCatalogue;

    private boolean proxySet = false;
    private String proxyHostName;
    private int proxyPort = -1;
    private int proxySSLPort = -1;
    private String proxyPrefix;

    /**
     * Init realm
     */
    public RealmWithConfigurationExceptionImpl(String name, String id, String mountPoint, File configFile, Exception configurationException) throws Exception {
        if (name != null) {
            this.name = name;
        } else {
            log.warn("No name was set within yanel realms config for realm with id: " + id);
            this.name = id;
        }
        this.name = this.name;
        this.id = id;
        this.mountPoint = mountPoint;
        this.configFile = configFile;
        this.configurationException = configurationException;
    }

    /**
     * Get configuration exception
     */
    public Exception getConfigurationException() {
        return configurationException;
    }

    /**
     * @see Realm#geName()
     */
    public String getName() {
        return name;
    }

    /**
     * Id of realm
     */
    public String getID() {
        return id;
    }

    /**
     * Mount point of realm
     */
    public String getMountPoint() {
        return mountPoint;
    }

    /**
     * Configuration file of realm.
     */
    public File getConfigFile() {
        return configFile;
    }

    /**
     *
     */
    public void setProxy(String hostName, int port, int sslPort, String prefix) {
        proxySet = true;
        proxyHostName = hostName;
        proxyPort = port;
        proxySSLPort = sslPort;
        proxyPrefix = prefix;
    }

    /**
     *
     */
    public boolean isProxySet() {
        return proxySet;
    }

    /**
     *
     */
    public String getProxyHostName() {
        return proxyHostName;
    }

    /**
     *
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     *
     */
    public int getProxySSLPort() {
        return proxySSLPort;
    }

    /**
     *
     */
    public String getProxyPrefix() {
        return proxyPrefix;
    }

    /**
     *
     */
    public String toString() {
        String descr = "Name: " + name + ", ID: " + id + ", Mount-Point: " + mountPoint;
        if (isProxySet()) {
            if (proxyHostName != null) {
                descr = descr + ", Reverse Proxy Host Name: " + proxyHostName;
            }
            if (proxyPort >= 0) {
                descr = descr + ", Reverse Proxy Port: " + proxyPort;
            } else {
                descr = descr + ", Reverse Proxy Port is set to default 80 (resp. -1)";
            }
            if (proxySSLPort >= 0) {
                descr = descr + ", Reverse Proxy SSL Port: " + proxySSLPort;
            } else {
                descr = descr + ", Reverse Proxy SSL Port is set to default 443 (resp. -1)";
            }
            if (proxyPrefix != null) {
               descr = descr + ", Reverse Proxy Prefix: " + proxyPrefix;
            }
        } else {
            descr = descr + ", No reverse proxy set";
        }
        return descr;
    }

    /**
     * Get data repository of realm
     */
    public Repository getRepository() throws Exception {
        return repository;
    }

    public void setRepository(Repository repository) throws Exception {
        this.repository = repository;
    }

    /**
     * Get RTI (Resource Type Identifier) repository of realm
     */
    public Repository getRTIRepository() throws Exception {
        return rtiRepository;
    }

    public void setRTIRepository(Repository repository) throws Exception {
        this.rtiRepository = repository;
    }

    /**
     *
     */
    public WebAuthenticator getWebAuthenticator() {
        return privateWebAuthenticator;
    }
    /**
     *
     */
    public void setWebAuthenticator(WebAuthenticator wa) {
        privateWebAuthenticator = wa;
    }

    public IdentityManager getIdentityManager() {
        return privateIdentityManager;
    }

    public void setIdentityManager(IdentityManager identityManager) {
        this.privateIdentityManager = identityManager;
    }

    /**
     * Get policy manager
     */
    public PolicyManager getPolicyManager() {
        return privatePolicyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        this.privatePolicyManager = policyManager;
    }

    /**
     * Get repository navigation
     */
    public Sitetree getRepoNavigation() {
        return repoNavigation;
    }

    /**
     * Get default language of this realm re content
     */
    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String language) {
        this.defaultLanguage = language;
    }

    /**
     * Please note that the root-dir element is optional
     * @deprecated
     */
    public File getRootDir() {
        log.warn("TODO: Try to avoid using the getRootDir() method because this method is deprecated!");
        return this.rootDir;
    }

    public void setRootDir(File rootDir) {
        this.rootDir = rootDir;
    }

    /**
     * Please note that the menu element is optional
     */
    public String getMenuClass() {
        try {
            Configuration realmConfig = new DefaultConfigurationBuilder().buildFromFile(getConfigFile());
            Configuration menuClassConfig = realmConfig.getChild("menu", false);
            if (menuClassConfig != null) {
                return menuClassConfig.getAttribute("class");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Gets a list of all languages supported by this realm.
     * @return list of languages. may be empty.
     */
    public String[] getLanguages() {
        return languages;
    }

    public void setLanguages(String[] languages) {
        //TODO: the cast should not be necessary. but under strange circumstances build fails without.
        this.languages = (String[]) languages.clone();
    }

    public TranslationManager getTranslationManager() {
        //log.debug("Translation Manager: " + translationManager.getClass().getName());
        return translationManager;
    }

    public void setTranslationManager(TranslationManager translationManager) {
        this.translationManager = translationManager;
    }

    public Repository getRepository(String id) throws Exception {
        Yanel yanel = Yanel.getInstance();
        RepositoryFactory extraRepoFactory = yanel.getRepositoryFactory(EXTRA_REPOSITORY_FACTORY_BEAN_ID);
        if (extraRepoFactory.exists(id)) {
            return extraRepoFactory.newRepository(id);
        } 
        return null;
    }

    public LanguageHandler getLanguageHandler() {
        return languageHandler;
    }

    public void setLanguageHandler(LanguageHandler languageHandler) {
        this.languageHandler = languageHandler;
    }

    /**
     *
     */
    public void destroy() throws Exception {
        log.warn("Shutdown realm: " + getName());
        getRepository().close();
        getRTIRepository().close();
        // TODO: close ac-identities and ac-policies repository?
    }

    /**
     * Get Default WebAuthenticator
     */
    private WebAuthenticator getDefaultWebAuthenticator() throws Exception {
        // TODO: Get this setting from spring config
        String defaultWebAuthenticatorImplClassName = "org.wyona.yanel.servlet.security.impl.DefaultWebAuthenticatorImpl";
        return (WebAuthenticator) Class.forName(defaultWebAuthenticatorImplClassName).newInstance();
    }
    
    /**
     * Gets the value of the i18n-catalogue config element.
     * This value normally is a URI pointing to an i18n message catalogue. 
     * @return i18n catalogue
     */
    public String getI18nCatalogue() {
        return this.i18nCatalogue;
    }


}
