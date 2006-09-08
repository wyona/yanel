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
import java.util.Properties;

import org.apache.log4j.Category;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

import org.wyona.commons.io.FileUtil;

/**
 *
 */
public class RealmConfiguration {

    private static Category log = Category.getInstance(RealmConfiguration.class);

    public static final String DEFAULT_CONFIGURATION_FILE = "yanel.properties";
    public static String CONFIGURATION_FILE = DEFAULT_CONFIGURATION_FILE;

    private URL propertiesURL;

    private File realmsConfigFile; 

    public String proxyHostName;
    public String proxyPort;
    public String proxyPrefix;

    java.util.HashMap hm = new java.util.HashMap();

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
            log.error("DEBUG: Realms Configuration: " + realmsConfigFile);
            readRealms();

            // TODO: This is actually a servlet thing and should be moved there ...
            proxyHostName = props.getProperty("proxy-host-name");
            proxyPort = props.getProperty("proxy-port");
            proxyPrefix = props.getProperty("proxy-prefix");
            log.debug("Proxy Settings: " + proxyHostName + ", " + proxyPort + ", " + proxyPrefix);
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
                log.error("DEBUG: " + realmElements[i].getAttribute("mount-point", null));
            }
        } catch (Exception e) {
            log.error(e);
        }
    }
}
