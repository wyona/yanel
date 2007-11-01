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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

import org.apache.log4j.Category;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.wyona.yanel.core.util.ConfigurationUtil;

/**
 * Abstraction of a resource configuration.
 */
public class ResourceConfiguration {

    private Category log = Category.getInstance(ResourceConfiguration.class);

    //protected Map properties;
    protected String name;
    protected String namespace;
    private String encoding = null;
    DefaultConfiguration config;
    
    /**
     *
     */
    public ResourceConfiguration(InputStream in) throws Exception {
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder(true);
        config = (DefaultConfiguration) builder.build(in);
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

        if (properties != null) {
            String LOCATION = "resource_config_location";
            String PREFIX = "yanel";
            String RC_NAMESPACE = "http://www.wyona.org/yanel/rti/1.0";
            config = new DefaultConfiguration("resource-config", LOCATION, RC_NAMESPACE, PREFIX);
            DefaultConfiguration rti = new DefaultConfiguration("rti", LOCATION, RC_NAMESPACE, PREFIX);
            rti.setAttribute("name", name);
            rti.setAttribute("namespace", namespace);
            config.addChild(rti);

            java.util.Iterator keyIterator = properties.keySet().iterator();
            while (keyIterator.hasNext()) {
                DefaultConfiguration property = new DefaultConfiguration("property", LOCATION, RC_NAMESPACE, PREFIX);
                String key = (String) keyIterator.next();
                property.setAttribute("name", key);
                property.setAttribute("value", (String) properties.get(key));
                config.addChild(property);
            }
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
                if (props[i].getAttribute("name") != null && props[i].getAttribute("name").equals(key)) {
                    return props[i].getAttribute("value");
                }
            }
        }
        return null;
    }
    
    /**
     * @param key
     * @return value for this key or null if no value exists for this key.
     */
    public String[] getProperties(String key) throws Exception {
        ArrayList properties = new ArrayList(); 
        if (config != null) {
            Configuration[] props = config.getChildren("property");
            for (int i = 0; i < props.length; i++) {
                if (props[i].getAttribute("name") != null && props[i].getAttribute("name").equals(key)) {
                	properties.add(props[i].getAttribute("value"));
                }
            }
            if (properties.size() < 1) return null; 
            return (String[]) properties.toArray(new String[0]);
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
     * Get yanel:custom-config. Returns null if resource config does not contain a custom-config element.
     */
    public org.w3c.dom.Document getCustomConfiguration() {
        Configuration customConfig = config.getChild("custom-config", false);
        try {
            return ConfigurationUtil.getCustomConfiguration(customConfig, customConfig.getName(), 
                customConfig.getNamespace());
        } catch (ConfigurationException ce) {
            log.warn(ce);
            return null;
        }
 
    }

 
}
