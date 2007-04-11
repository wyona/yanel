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

import org.wyona.commons.io.Path;
import org.wyona.security.core.api.IdentityManager;
import org.wyona.security.core.api.PolicyManager;
import org.wyona.yarep.core.Repository;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

import org.apache.log4j.Category;

/**
 *
 */
public class Realm {

    private Category log = Category.getInstance(Realm.class);

    private String name;
    private String id;
    private String mountPoint;
    private String defaultLanguage;
    private Repository repository;
    private Repository rtiRepository;
    private PolicyManager policyManager;
    private IdentityManager identityManager;
    private File configFile;

    private String proxyHostName;
    private String proxyPort;
    private String proxyPrefix;

    /**
     *
     */
    public Realm(String name, String id, String mountPoint, File configFile) {
        this.name = name;
        this.id = id;
        this.mountPoint = mountPoint;
        this.configFile = configFile;
    }

    /**
     * Name of realm
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
    public void setProxy(String hostName, String port, String prefix) {
        proxyHostName = hostName;
        proxyPort = port;
        proxyPrefix = prefix;
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
    public String getProxyPort() {
        return proxyPort;
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
        if (proxyHostName != null) {
            descr = descr + ", Reverse Proxy Host Name: " + proxyHostName;
        }
        if (proxyPort != null) {
            descr = descr + ", Reverse Proxy Port: " + proxyPort;
        }
        if (proxyPrefix != null) {
            descr = descr + ", Reverse Proxy Prefix: " + proxyPrefix;
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

    public IdentityManager getIdentityManager() {
        return identityManager;
    }

    public void setIdentityManager(IdentityManager identityManager) {
        this.identityManager = identityManager;
    }

    public PolicyManager getPolicyManager() {
        return policyManager;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        this.policyManager = policyManager;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String language) {
        this.defaultLanguage = language;
    }

    /**
     * Please note that the root-dir element is optional
     */
    public File getRootDir() {
        try {
            Configuration realmConfig = new DefaultConfigurationBuilder().buildFromFile(getConfigFile());
            Configuration rootDirConfig = realmConfig.getChild("root-dir", false);
            if (rootDirConfig != null) {
                File rootDirFile = new File(rootDirConfig.getValue());
                if (!rootDirFile.isAbsolute()) {
                    return new File(org.wyona.commons.io.FileUtil.concat(getConfigFile().getParent(), rootDirFile.toString()));
                } else {
                    return rootDirFile;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
