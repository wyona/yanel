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
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.apache.log4j.Category;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

import org.wyona.commons.io.FileUtil;
import org.wyona.security.core.IdentityManagerFactory;
import org.wyona.security.core.PolicyManagerFactory;
import org.wyona.security.core.api.IdentityManager;
import org.wyona.security.core.api.PolicyManager;
import org.wyona.yanel.core.ConfigurationException;
import org.wyona.yanel.core.Yanel;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;

/**
 *
 */
public class RealmConfiguration {

    private static Category log = Category.getInstance(RealmConfiguration.class);

    public static final String DEFAULT_CONFIGURATION_FILE = "yanel.properties";
    public static String CONFIGURATION_FILE = DEFAULT_CONFIGURATION_FILE;

    private URL propertiesURL;

    private File realmsConfigFile; 

    private LinkedHashMap hm = new LinkedHashMap();
    private Realm rootRealm = null;

    /**
     *
     */
    public RealmConfiguration() throws ConfigurationException {
        this(DEFAULT_CONFIGURATION_FILE);
    }

    /**
     *
     */
    public RealmConfiguration(String configurationFile) throws ConfigurationException {
        CONFIGURATION_FILE = configurationFile;

        propertiesURL = RealmConfiguration.class.getClassLoader().getResource(CONFIGURATION_FILE);
        if (propertiesURL == null) {
            log.error("No such resource: " + CONFIGURATION_FILE);
            return;
        }

        Properties props = new Properties();
        try {
            props.load(propertiesURL.openStream());
            File propsFile = new File(propertiesURL.getFile());

            realmsConfigFile = new File(props.getProperty("realms-config"));
            if (!realmsConfigFile.isAbsolute()) {
                realmsConfigFile = FileUtil.file(propsFile.getParentFile().getAbsolutePath(), realmsConfigFile.toString());
            }
            log.debug("Realms Configuration: " + realmsConfigFile);
            readRealms();
            //assignRepositories();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ConfigurationException("Could not load realms configuration file: " + propertiesURL);
        }
    }

    /**
     *
     */
    public String getConfigurationFile() {
        return CONFIGURATION_FILE;
    }

    /**
     *
     */
    public void readRealms() throws ConfigurationException {
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration config;

        try {
            Yanel yanel = Yanel.getInstance();
            PolicyManagerFactory pmFactory = (PolicyManagerFactory) yanel.getBeanFactory().getBean("PolicyManagerFactory");
            IdentityManagerFactory imFactory = (IdentityManagerFactory) yanel.getBeanFactory().getBean("IdentityManagerFactory");

            RepositoryFactory repoFactory = yanel.getRepositoryFactory("DefaultRepositoryFactory");
            RepositoryFactory rtiRepoFactory = yanel.getRepositoryFactory("RTIRepositoryFactory");
            RepositoryFactory policiesRepoFactory = yanel.getRepositoryFactory("ACPoliciesRepositoryFactory");
            RepositoryFactory identitiesRepoFactory = yanel.getRepositoryFactory("ACIdentitiesRepositoryFactory");
            config = builder.buildFromFile(realmsConfigFile);
            
            Configuration[] realmElements = config.getChildren("realm");
            for (int i = 0;i < realmElements.length; i++) {
                String mountPoint = realmElements[i].getAttribute("mount-point", null);
                String realmId = realmElements[i].getAttribute("id", null);
                String rootFlag = realmElements[i].getAttribute("root", "false");
                Configuration name = realmElements[i].getChild("name", false);
                Realm realm = new Realm(name.getValue(), realmId, new org.wyona.commons.io.Path(mountPoint));
                Configuration proxy = realmElements[i].getChild("reverse-proxy", false);
                if (proxy != null) {
                    realm.setProxy(proxy.getChild("host-name").getValue(), proxy.getChild("port").getValue(""), proxy.getChild("prefix").getValue());
                }
                Configuration configElement = realmElements[i].getChild("config", false);
                if (configElement == null) {
                    throw new ConfigurationException("Missing <config src=\"...\"/> child element for realm " + realmId);
                }
                String configSrc = configElement.getAttribute("src", null);
                
                File realmConfigFile = resolveFile(new File(configSrc), realmsConfigFile);
                log.debug("Reading realm config file for [" + realmId + "]: " + realmConfigFile);
                Configuration realmConfig = builder.buildFromFile(realmConfigFile);
                
                String repoConfigSrc = realmConfig.getChild("data", false).getValue();
                File repoConfig = resolveFile(new File(repoConfigSrc), realmConfigFile);
                realm.setRepository(repoFactory.newRepository(realmId, repoConfig));
                
                repoConfigSrc = realmConfig.getChild("rti", false).getValue();
                repoConfig = resolveFile(new File(repoConfigSrc), realmConfigFile);
                realm.setRTIRepository(rtiRepoFactory.newRepository(realmId, repoConfig));
                
                Configuration repoConfigElement = realmConfig.getChild("ac-policies", false);
                if (repoConfigElement != null) {
                    repoConfig = resolveFile(new File(repoConfigElement.getValue()), realmConfigFile);
                    Repository policiesRepo = policiesRepoFactory.newRepository(realmId, repoConfig);
                    PolicyManager policyManager = pmFactory.newPolicyManager(policiesRepo);
                    realm.setPolicyManager(policyManager);
                }
                
                repoConfigElement = realmConfig.getChild("ac-identities", false);
                if (repoConfigElement != null) {
                    repoConfig = resolveFile(new File(repoConfigElement.getValue()), realmConfigFile);
                    Repository identitiesRepo = identitiesRepoFactory.newRepository(realmId, repoConfig);
                    IdentityManager identityManager = imFactory.newIdentityManager(identitiesRepo);
                    realm.setIdentityManager(identityManager);
                }
                
                log.info("Realm: " + realm);
                
                hm.put(realmId, realm);
                if (rootFlag.equals("true")) {
                    log.debug("Root realm found: " + realm.getID());
                    if (rootRealm == null) {
                        log.debug("Root realm set: " + realm.getID());
                        rootRealm = realm;
                    } else {
                        log.error("Root realm has already been set: " + realmId);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ConfigurationException("Error reading realm configuration from file " + 
                    realmsConfigFile + ": " + e.getMessage(), e);
        }
        inheritRootRealmProperties();
    }
    
    /**
     * If the given file has a relative path, resolve it relative to the given dir.
     * If dir is in fact a file, the resolving will use the parent dir of that file.  
     * @param file
     * @param dir
     * @return
     */
    protected File resolveFile(File file, File dir) {
        if (!file.isAbsolute()) {
            if (dir.isDirectory()) {
                file = FileUtil.file(dir.getAbsolutePath(), file.toString());
            } else {
                file = FileUtil.file(dir.getParentFile().getAbsolutePath(), file.toString());
            }
        }
        return file;
    }

    /**
     *
     */
    public Realm getRealm(String id) {
        return (Realm) hm.get(id);
    }

    /**
     * Get all realms in the order they given in the (local.)yanel.properties file.
     */
    public Realm[] getRealms() {
        Realm[] realms = new Realm[hm.size()];
        return realms = (Realm[])hm.values().toArray(realms);
    }

    /**
     *
     */
    public Realm getRootRealm() {
        return rootRealm;
    }

    /**
     * Add new realm (e.g. third party realm)
     */
    public void addRealm() {
        log.error("Not implemented yet!");
    }

    /**
     *
     */
    private void inheritRootRealmProperties() {
        java.util.Iterator keyIterator = hm.keySet().iterator();
        while(keyIterator.hasNext()) {
            String key = (String)keyIterator.next();
            Realm realm = (Realm)hm.get(key);
            if ((realm.getProxyHostName() == null) && (!key.equals(rootRealm.getID()))) {
                realm.setProxy(rootRealm.getProxyHostName(), rootRealm.getProxyPort(), rootRealm.getProxyPrefix());
                log.debug("Inherit root realm properties to realm: " + key);
            }
            if (realm.getIdentityManager() == null) {
                realm.setIdentityManager(rootRealm.getIdentityManager());
            }
            if (realm.getPolicyManager() == null) {
                realm.setPolicyManager(rootRealm.getPolicyManager());
            }
        }
    }
    
}
