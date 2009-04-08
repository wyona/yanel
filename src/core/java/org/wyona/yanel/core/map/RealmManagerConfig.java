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

                Configuration configElement = realmElements[i].getChild("config", false);
                if (configElement == null) {
                    throw new ConfigurationException("Missing <config src=\"...\"/> child element for realm " + realmId);
                }
                String configSrc = configElement.getAttribute("src", null);

                RealmContextConfig rcc = new RealmContextConfig(realmId, mountPoint, new Boolean(rootFlag).booleanValue(), new File(configSrc));

                Configuration nameConfig = realmElements[i].getChild("name", false);
                if (nameConfig != null) rcc.setLabel(nameConfig.getValue());

                Configuration proxy = realmElements[i].getChild("reverse-proxy", false);
                if (proxy != null) {
                    int proxyPort = new Integer(proxy.getChild("port").getValue("-1")).intValue();
                    int proxySSLPort = new Integer(proxy.getChild("ssl-port").getValue("-1")).intValue();
                    String prefixValue = proxy.getChild("prefix").getValue("");
                    if (prefixValue.length() == 0) prefixValue = null;
                    log.debug("Prefix value: " + prefixValue);
                    String hostName = proxy.getChild("host-name").getValue();
                    rcc.setReverseProxyConfig(new ReverseProxyConfig(hostName, proxyPort, proxySSLPort, prefixValue));
                }

                realmContextConfigsVec.add(rcc);
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
    }
}
