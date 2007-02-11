/*
 * Copyright 2007 Wyona
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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

import org.apache.log4j.Category;

/**
 * Abstraction of a resource configuration.
 */
public class ResourceConfiguration {

    private Category log = Category.getInstance(ResourceConfiguration.class);

    protected Map properties;
    protected String name;
    protected String namespace;
    private String encoding = null;
    Configuration config;
    
    /**
     *
     */
    public ResourceConfiguration(InputStream in) throws Exception {
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder(true);
        config = builder.build(in);
        Configuration rtiConfig = config.getChild("rti");
        name = rtiConfig.getAttribute("name");
        namespace = rtiConfig.getAttribute("namespace");
        log.debug("Universal Name: " + getUniversalName());

        Configuration encodingConfig = config.getChild("encoding", false);
        if (encodingConfig != null) encoding = encodingConfig.getValue();

        // TODO: Read properties and set this.properties
    }
    
    /**
     * Create a resource from scratch
     * @param name Resource Type Name
     * @param namespace Resource Type Namespace
     */
    public ResourceConfiguration(String name, String namespace, Map properties) {
        this.name = name;
        this.namespace = namespace;

        if (properties == null) {
            this.properties = new HashMap();
        } else {
            this.properties = properties;
        }
    }
    
    /**
     * Get universal name of resource type
     */
    public String getUniversalName() {
	return "<{" + namespace + "}" + name + "/>";
    }
    
    /**
     * Get resource type name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get resource type namespace
     */
    public String getNamespace() {
        return namespace;
    }
    
    /**
     * Get encoding respectively charset
     */
    public String getEncoding() {
        return encoding;
    }
    
    /**
     * @param key
     * @return value for this key or null if no value exists for this key.
     */
    public String getProperty(String key) throws Exception {
        //return (String)properties.get(key);
        if (config != null) {
            Configuration[] props = config.getChildren("property");
            for (int i = 0; i < props.length; i++) {
                if (props[i].getAttribute("name") != null && props[i].getAttribute("name").equals(key)) return props[i].getAttribute("value");
            }
        }
        return null;
    }
    
    /**
     * Check if property exists
     */
    public boolean containsKey(String key) throws Exception {
        //return properties.containsKey(key);
        if (config != null) {
            Configuration[] props = config.getChildren("property");
            for (int i = 0; i < props.length; i++) {
                if (props[i].getAttribute("name") != null && props[i].getAttribute("name").equals(key)) return true;
            }
        }
        return false;
    }

    /**
     * Get yanel:custom-config
     */
    public org.w3c.dom.Document getCustomConfiguration() {
        Configuration customConfig = config.getChild("custom-config");
        if (customConfig != null) {
            log.error("DEBUG: Custom Configuration: " + customConfig);
        } else {
            log.warn("No custom configuration: " + getUniversalName());
        }
        return null;
    }
}
