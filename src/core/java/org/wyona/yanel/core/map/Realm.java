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

/**
 * Realm interface
 * Please make sure to implement a constructor like Realm(String, String, String, File), because RealmManager does instantiate such a constructor dynamically
 */
public interface Realm {

    /**
     * Name of realm
     */
    public String getName();

    /**
     * Id of realm
     */
    public String getID();

    /**
     * Mount point of realm
     */
    public String getMountPoint();

    /**
     * Configuration file of realm.
     */
    public File getConfigFile();

    /**
     *
     */
    public void setProxy(String hostName, int port, int sslPort, String prefix);

    /**
     *
     */
    public boolean isProxySet();

    /**
     *
     */
    public String getProxyHostName();

    /**
     *
     */
    public int getProxyPort();

    /**
     *
     */
    public int getProxySSLPort();

    /**
     *
     */
    public String getProxyPrefix();

    /**
     * Get data repository of realm
     */
    public Repository getRepository() throws Exception;

    public void setRepository(Repository repository) throws Exception;

    /**
     * Get RTI (Resource Type Identifier) repository of realm
     */
    public Repository getRTIRepository() throws Exception;

    public void setRTIRepository(Repository repository) throws Exception;

    /**
     *
     */
    public WebAuthenticator getWebAuthenticator();

    /**
     *
     */
    public void setWebAuthenticator(WebAuthenticator wa);

    public IdentityManager getIdentityManager();

    public void setIdentityManager(IdentityManager identityManager);

    /**
     * Get policy manager
     */
    public PolicyManager getPolicyManager();

    public void setPolicyManager(PolicyManager policyManager);

    /**
     * Get repository navigation
     */
    public Sitetree getRepoNavigation();

    /**
     * Get default language of this realm re content
     */
    public String getDefaultLanguage();

    public void setDefaultLanguage(String language);

    /**
     * Please note that the root-dir element is optional
     * @deprecated
     */
    public File getRootDir();

    public void setRootDir(File rootDir);

    /**
     * Please note that the menu element is optional
     */
    public String getMenuClass();

    /**
     * Gets a list of all languages supported by this realm.
     * @return list of languages. may be empty.
     */
    public String[] getLanguages();

    public void setLanguages(String[] languages);

    public TranslationManager getTranslationManager();

    public void setTranslationManager(TranslationManager translationManager);

    /**
     * Get repository from additional repository config (see http://www.yanel.org/en/documentation/realm/realm-configuration.html#yarep-repos)
     * @param id Repository id
     */
    public Repository getRepository(String id) throws Exception;

    public LanguageHandler getLanguageHandler();

    public void setLanguageHandler(LanguageHandler languageHandler);

    /**
     *
     */
    public void destroy() throws Exception;

    /**
     * Gets the value of the i18n-catalogue config element.
     * This value normally is a URI pointing to an i18n message catalogue. 
     * @return i18n catalogue
     */
    public String getI18nCatalogue();
}
