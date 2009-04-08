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

import org.apache.log4j.Logger;

import org.wyona.yanel.core.ConfigurationException;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

/*
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Properties;


import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.wyona.commons.io.FileUtil;
import org.wyona.yanel.core.Yanel;
import org.wyona.yarep.core.RepositoryFactory;
*/

/**
 * Class providing access to realms configuration
 */
public class RealmManagerConfig {

    private static Logger log = Logger.getLogger(RealmManagerConfig.class);

    private File realmsConfigFile;

    /**
     * Get realms configuration file
     */
    public File getConfigurationFile() {
        return realmsConfigFile;
    }

    /**
     * Read realms configuration
     * @param realmsConfigFile Realms configuration file, where several realms (location, etc.) are configured
     */
    public RealmContextConfig[] getRealmContextConfigs(File realmsConfigFile) throws ConfigurationException {

        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration config;
        java.util.Vector realmContextConfigsVec = new java.util.Vector();
        try {
            log.info("Read realms configuration: " + realmsConfigFile);
            config = builder.buildFromFile(realmsConfigFile);
            Configuration[] realmElements = config.getChildren("realm");
            for (int i = 0;i < realmElements.length; i++) {
                String realmId = realmElements[i].getAttribute("id", null);

                String mountPoint = realmElements[i].getAttribute("mount-point", null);

                String rootFlag = realmElements[i].getAttribute("root", "false");

                Configuration nameConfig = realmElements[i].getChild("name", false);
                String name = null;
                if (nameConfig != null) name = nameConfig.getValue();

                Configuration configElement = realmElements[i].getChild("config", false);
                if (configElement == null) {
                    throw new ConfigurationException("Missing <config src=\"...\"/> child element for realm " + realmId);
                }
                String configSrc = configElement.getAttribute("src", null);

                realmContextConfigsVec.add(new RealmContextConfig(realmId, mountPoint, Boolean.getBoolean(rootFlag), new File(configSrc)));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ConfigurationException("Error reading realms config from file " + 
                    realmsConfigFile + ": " + e, e);
        }

        RealmContextConfig[] realmContextConfigs = new RealmContextConfig[realmContextConfigsVec.size()];
        for (int i = 0;i < realmContextConfigs.length; i++) {
            realmContextConfigs[i] = (RealmContextConfig) realmContextConfigsVec.elementAt(i);
        }
        return realmContextConfigs;

/*
        hm = new LinkedHashMap();
        rootRealm = null;

        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration config;

        Yanel yanel;
        try {
            yanel = Yanel.getInstance();
        } catch (Exception e) {
            String errorMsg = "Could not initialize yanel: " + e.getMessage();
            log.error(errorMsg, e);
            throw new ConfigurationException(errorMsg, e);
        }
        
        try {
            log.debug("Get default repo factory ...");
            RepositoryFactory defaultRepoFactory = yanel.getRepositoryFactory(Realm.DEFAULT_REPOSITORY_FACTORY_BEAN_ID);
            defaultRepoFactory.reset();
            log.debug("Get default repo factory DONE.");

            RepositoryFactory rtiRepoFactory = yanel.getRepositoryFactory("RTIRepositoryFactory");
            rtiRepoFactory.reset();

            RepositoryFactory policiesRepoFactory = yanel.getRepositoryFactory("ACPoliciesRepositoryFactory");
            policiesRepoFactory.reset();

            RepositoryFactory identitiesRepoFactory = yanel.getRepositoryFactory("ACIdentitiesRepositoryFactory");
            identitiesRepoFactory.reset();

            log.info("Read realms configuration: " + realmsConfigFile);
            config = builder.buildFromFile(realmsConfigFile);
            Configuration[] realmElements = config.getChildren("realm");
            for (int i = 0;i < realmElements.length; i++) {
                String mountPoint = realmElements[i].getAttribute("mount-point", null);
                String realmId = realmElements[i].getAttribute("id", null);
                String rootFlag = realmElements[i].getAttribute("root", "false");
                Configuration nameConfig = realmElements[i].getChild("name", false);
                String name = null;
                if (nameConfig != null) name = nameConfig.getValue();
                Configuration configElement = realmElements[i].getChild("config", false);
                if (configElement == null) {
                    throw new ConfigurationException("Missing <config src=\"...\"/> child element for realm " + realmId);
                }
                String configSrc = configElement.getAttribute("src", null);
                
                File realmConfigFile = resolveFile(new File(configSrc), realmsConfigFile);
                if (realmConfigFile.isDirectory()) {
                    realmConfigFile = new File(realmConfigFile, REALM_DEFAULT_CONFIG_NAME);
                }

                try {
                    log.info("Reading realm config file for [" + realmId + "]: " + realmConfigFile);
                    Configuration realmConfig = builder.buildFromFile(realmConfigFile);
                    Realm realm;
                    try {
                        String customRealmImplClassName = realmConfig.getAttribute("class");
                        Class[] classArgs = new Class[]{String.class, String.class, String.class, File.class};
                        Object[] values = new Object[4];
                        values[0] = name;
                        values[1] = realmId;
                        values[2] = mountPoint;
                        values[3] = realmConfigFile;
                        java.lang.reflect.Constructor ct = Class.forName(customRealmImplClassName).getConstructor(classArgs);
                        realm = (Realm) ct.newInstance(values);
                    } catch(ClassNotFoundException e) {
                        log.error("Class not found: " + e.getMessage() + ". Fallback to default realm implementation!");
                        realm = new Realm(name, realmId, mountPoint, realmConfigFile);
                    } catch(Exception e) {
                        log.info("Default realm implementation will be used.");
                        realm = new Realm(name, realmId, mountPoint, realmConfigFile);
                    }
                    
                    Configuration proxy = realmElements[i].getChild("reverse-proxy", false);
                    if (proxy != null) {
                        int proxyPort = new Integer(proxy.getChild("port").getValue("-1")).intValue();
                        int proxySSLPort = new Integer(proxy.getChild("ssl-port").getValue("-1")).intValue();
                        String prefixValue = proxy.getChild("prefix").getValue("");
                        if (prefixValue.length() == 0) prefixValue = null;
                        log.debug("Prefix value: " + prefixValue);
                        realm.setProxy(proxy.getChild("host-name").getValue(), proxyPort, proxySSLPort, prefixValue);
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
                } catch (Exception e) {
                    String errorMsg = "Error setting up realm [" + realmId + "]: " + realmConfigFile + ": " + e;
                    log.error(errorMsg, e);
                    // NOTE: Do not throw an exception, because otherwise all other realms are not being loaded either
                    // TODO/TBD: Maybe one should enhance Realm by a method such as setStatus() and getStatus() in order to check if a realm has been registered successfully or not!
                    //throw new ConfigurationException(errorMsg, e);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ConfigurationException("Error setting up realms from file " + 
                    realmsConfigFile + ": " + e, e);
        }
        inheritRootRealmProperties();
*/
    }
}
