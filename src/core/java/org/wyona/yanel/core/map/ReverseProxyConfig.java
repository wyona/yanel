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

/**
 * Class providing access to reverse proxy configuration
 */
public class ReverseProxyConfig {

    private static Logger log = Logger.getLogger(ReverseProxyConfig.class);

    private String hostName;
    private int port;
    private int sslPort;
    private String prefix;

    /**
     * @param hostName Host name
     * @param port Port
     * @param sslPort SSL port
     * @param prefix Prefix
     */
    public ReverseProxyConfig(String hostName, int port, int sslPort, String prefix) {
        this.hostName = hostName;
        this.port = port;
        this.sslPort = sslPort;
        this.prefix = prefix;
    }

    /**
     * Get port
     */
    public int getPort() {
        return port;
    }

    /**
     * Get SSL port
     */
    public int getSSLPort() {
        return sslPort;
    }

    /**
     * Get prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Get host name
     */
    public String getHostName() {
        return hostName;
    }

    /**
     *
     */
    public String toString() {
        String s = "Reverse proxy configuration:\n";
        s = s + "  Host name = " + hostName + "\n";
        s = s + "  Port = " + port + "\n";
        s = s + "  SSL port = " + sslPort + "\n";
        s = s + "  prefix = " + prefix;
        return s;
    }
}
