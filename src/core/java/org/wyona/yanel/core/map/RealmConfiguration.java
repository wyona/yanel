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
import org.wyona.yanel.core.Yanel;
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
    public RealmConfiguration() {
        this(DEFAULT_CONFIGURATION_FILE);
    }

    /**
     *
     */
    public RealmConfiguration(String configurationFile) {
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
            assignRepositories();
        } catch (Exception e) {
            log.error(e);
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
    public void readRealms() {
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration config;

        try {
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
                log.debug("Realm: " + realm);
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
            log.error(e);
        }
        inheritRootRealmProperties();
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
        }
    }
    
    /**
     * Assigns the repositories to the realms.
     * Each realm has a default data repository and a rti repository.
     * The id of the realm matches the id of the repository.
     */
    private void assignRepositories() throws Exception {
        RepositoryFactory defaultRepoFactory = Yanel.getInstance().getRepositoryFactory("DefaultRepositoryFactory");
        RepositoryFactory rtiRepoFactory = Yanel.getInstance().getRepositoryFactory("RTIRepositoryFactory");
        
        java.util.Iterator keyIterator = hm.keySet().iterator();
        while(keyIterator.hasNext()) {
            String key = (String)keyIterator.next();
            Realm realm = (Realm)hm.get(key);
            
            realm.setRepository(defaultRepoFactory.newRepository(realm.getID()));
            realm.setRTIRepository(rtiRepoFactory.newRepository(realm.getID()));
        }
        
    }
}
