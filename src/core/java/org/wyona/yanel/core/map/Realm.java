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

import org.wyona.commons.io.Path;
import org.wyona.yarep.core.Repository;

/**
 *
 */
public class Realm {

    private String name;
    private String id;
    private Path mountPoint;
    private Repository repository;
    private Repository rtiRepository;

    private String proxyHostName;
    private String proxyPort;
    private String proxyPrefix;

    /**
     *
     */
    public Realm(String name, String id, Path mountPoint) {
        this.name = name;
        this.id = id;
        this.mountPoint = mountPoint;
    }

    /**
     *
     */
    public String getName() {
        return name;
    }

    /**
     *
     */
    public String getID() {
        return id;
    }

    /**
     *
     */
    public Path getMountPoint() {
        return mountPoint;
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
    
    public Repository getRepository() throws Exception {
        return repository;
    }
    
    public void setRepository(Repository repository) throws Exception {
        this.repository = repository;
    }

    public Repository getRTIRepository() throws Exception {
        return rtiRepository;
    }
    
    public void setRTIRepository(Repository repository) throws Exception {
        this.rtiRepository = repository;
    }


}
