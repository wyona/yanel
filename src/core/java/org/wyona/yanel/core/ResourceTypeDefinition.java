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

package org.wyona.yanel.core;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

/**
 * Contains universal name and the class name of the implementation of a resource
 */
public class ResourceTypeDefinition {

    private Logger log = LogManager.getLogger(ResourceTypeDefinition.class);

    private String uname;
    private String classname;
    private String description;

    private File configFile;

    // TODO: In order to resolve other stuff, e.g. XSLTs, htdocs, icons, ...
    private java.net.URL configURL;

    /**
     *
     */
    public ResourceTypeDefinition(File file) throws Exception {
        this(new java.io.FileInputStream(file));
        this.configFile = file;
        // TODO: Set configURL
    }

    /**
     * Load resource type definition from URL
     * @param url URL pointing to resource type definition
     */
    public ResourceTypeDefinition(java.net.URL url) throws Exception {
        this.configURL = url;
        // TODO: Throw exception instead catching it
        try {
            initRTD(url.openStream());
        } catch(Exception e) {
            log.error("Failed to load '" + url + "' ...");
            log.error(e, e);
        }
    }

    /**
     * Load resource type definition from input stream
     * @param in InputStream containing resource type definition
     */
    public ResourceTypeDefinition(java.io.InputStream in) {
        initRTD(in);
    }

    /**
     *
     */
    private void initRTD(java.io.InputStream in) {
        // TODO: Set configURL
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration config;
        try {
            config = builder.build(in);
            String localName = config.getAttribute("name", null);
            String namespace= config.getAttribute("namespace", null);
            uname = "<{" + namespace + "}" + localName + "/>";
            classname = config.getAttribute("class", null);
            description = config.getChild("description").getValue();
        } catch(Exception e) {
            log.error(e, e);
        }
    }

    /**
     * Get universal name
     */
    public String getResourceTypeUniversalName() {
        return uname;
    }

    /**
     * Get local name
     */
    public String getResourceTypeLocalName() {
        return uname.substring(uname.indexOf("}") + 1, uname.length() -2);
    }

    /**
     * Get namespace
     */
    public String getResourceTypeNamespace() {
        return uname.substring(uname.indexOf("{") + 1, uname.indexOf("}"));
    }

    /**
     *
     */
    public String getResourceTypeClassname() {
        return classname;
    }

    /**
     *
     */
    public String getResourceTypeDescription() {
        return description;
    }

    /**
     *
     */
    public File getConfigFile() {
        if (configFile == null) {
            log.warn("Config file is null, because resource type definition of '" + getResourceTypeUniversalName() + "' has been probably loaded as part of a jar file! TODO: Offer as alternative the jar URI.");
        }
        return configFile;
    }

    /**
     *
     */
    public String toString() {
        return uname;
    }
}
