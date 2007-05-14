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
import java.lang.ClassNotFoundException;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.log4j.Category;

import org.wyona.commons.io.FileUtil;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.map.RealmConfiguration;

/**
 *
 */
public class ResourceTypeRegistry {

    private static Category log = Category.getInstance(ResourceTypeRegistry.class);

    public static final String DEFAULT_CONFIGURATION_FILE = "yanel.properties";
    public static final String DEFAULT_CONFIGURATION_FILE_XML = "yanel.xml";
    public static String CONFIGURATION_FILE = DEFAULT_CONFIGURATION_FILE;

    public static String RESOURCE_DEFAULT_CONFIG_NAME = "resource.xml";

    private URL propertiesURL;
    private File configFile;
    private File resourceTypeConfigFile; 

    java.util.HashMap hm = new java.util.HashMap();

    /**
     *
     */
    public ResourceTypeRegistry() {
        this(DEFAULT_CONFIGURATION_FILE_XML);
    }

    /**
     * 
     */
    public ResourceTypeRegistry(String configurationFile) {
        CONFIGURATION_FILE = configurationFile;

        if (RealmConfiguration.class.getClassLoader().getResource(CONFIGURATION_FILE) == null) {
            CONFIGURATION_FILE = DEFAULT_CONFIGURATION_FILE;
        }

        if (ResourceTypeRegistry.class.getClassLoader().getResource(CONFIGURATION_FILE) != null) {

            if (CONFIGURATION_FILE.endsWith(".xml")) {
                configFile = new File(RealmConfiguration.class.getClassLoader()
                        .getResource(CONFIGURATION_FILE)
                        .getFile());
                try {
                    DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
                    Configuration config;
                    config = builder.buildFromFile(configFile);

                    resourceTypeConfigFile = new File(config.getChild("resource-types-config")
                            .getAttribute("src"));
                    if (!resourceTypeConfigFile.isAbsolute()) {
                        resourceTypeConfigFile = FileUtil.file(configFile.getParentFile()
                                .getAbsolutePath(), resourceTypeConfigFile.toString());
                    }
                    log.debug("Realms Configuration: " + resourceTypeConfigFile);
                    readResourceTypes();
                } catch (Exception e) {
                    String errorMsg = "Failure while reading configuration: " + e.getMessage();
                    log.error(errorMsg, e);
                }


            } else if (CONFIGURATION_FILE.endsWith("properties")) {
                log.warn("DEPRECATED: " + CONFIGURATION_FILE);

                propertiesURL = ResourceTypeRegistry.class.getClassLoader().getResource(CONFIGURATION_FILE);

                Properties props = new Properties();
                try {
                    props.load(propertiesURL.openStream());
                    // use URLDecoder to avoid problems when the filename contains spaces, see
                    // http://bugzilla.wyona.com/cgi-bin/bugzilla/show_bug.cgi?id=5165
                    File propsFile = new File(URLDecoder.decode(propertiesURL.getFile()));
                    String separator = ",";
                    String[] tokens = props.getProperty("resources").split(separator);
                    for (int i = 0; i < tokens.length; i++) {
                        File resConfigFile = new File(tokens[i]);
                        if (!resConfigFile.isAbsolute()) {
                            resConfigFile = FileUtil.file(propsFile.getParentFile().getAbsolutePath(), tokens[i]);
                        }

                        if (resConfigFile.isDirectory()) {
                            resConfigFile = new File(resConfigFile, RESOURCE_DEFAULT_CONFIG_NAME);
                        }

                        if (resConfigFile.isFile()) {
                            ResourceTypeDefinition rtd = new ResourceTypeDefinition(resConfigFile);
                            log.debug("Universal Name: " + rtd.getResourceTypeUniversalName());
                            log.debug("Classname: " + rtd.getResourceTypeClassname());
                            hm.put(rtd.getResourceTypeUniversalName(), rtd);
                        } else {
                            log.error("No such file or directory: " + resConfigFile);
                        }
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            } else {
                log.error(CONFIGURATION_FILE + "have to be either .xml or .properties");
            }
        } else {
            log.error("No such config file" + CONFIGURATION_FILE);
        }
    }

    /**
     * 
     */
    public void readResourceTypes() throws ConfigurationException {
        try {
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
            Configuration config;
            config = builder.buildFromFile(resourceTypeConfigFile);
            
            Configuration resourceTypes[] = config.getChildren("resource-type");
            
            for (int i = 0; i < resourceTypes.length; i++) {
                File resConfigFile = new File(resourceTypes[i].getAttribute("src"));
                if (!resConfigFile.isAbsolute()) {
                    resConfigFile = FileUtil.file(resourceTypeConfigFile.getParentFile().getAbsolutePath(), resourceTypes[i].getAttribute("src"));
                }

                if (resConfigFile.isDirectory()) {
                    resConfigFile = new File(resConfigFile, RESOURCE_DEFAULT_CONFIG_NAME);
                }

                if (resConfigFile.isFile()) {
                    ResourceTypeDefinition rtd = new ResourceTypeDefinition(resConfigFile);
                    log.debug("Universal Name: " + rtd.getResourceTypeUniversalName());
                    log.debug("Classname: " + rtd.getResourceTypeClassname());
                    hm.put(rtd.getResourceTypeUniversalName(), rtd);
                } else {
                    log.error("No such file or directory: " + resConfigFile);
                }
            }    
        } catch (Exception e) {
            String errorMsg = "Failure while reading configuration: " + e.getMessage(); 
            log.error(errorMsg, e);
            throw new ConfigurationException(errorMsg, e);
        }
        
    }
   
    /**
     *
     */
    public ResourceTypeDefinition getResourceTypeDefinition(String universalName) throws Exception {
        if (!hm.containsKey(universalName)) {
            throw new Exception("Unknown resource type: " + universalName);
        }
        return (ResourceTypeDefinition) hm.get(universalName);
    }

    /**
     *
     */
    public ResourceTypeDefinition[] getResourceTypeDefinitions() {
        java.util.Set keys = hm.keySet();
        java.util.Iterator keysIterator = keys.iterator();
        ResourceTypeDefinition[] rtds = new ResourceTypeDefinition[keys.size()];
        int i = 0;
        while (keysIterator.hasNext()) {
            String universalName = (String) keysIterator.next();
            rtds[i] = (ResourceTypeDefinition)hm.get(universalName);
            i++;
        }
        return rtds;
    }

    /**
     * @deprecated
     */
    public Resource newResource(String universalName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    ResourceTypeDefinition rtd = (ResourceTypeDefinition) hm.get(universalName);
        if (rtd != null) {
            Resource resource = (Resource) Class.forName(rtd.getResourceTypeClassname()).newInstance();

            resource.setRTD(rtd);
            // TODO: Set Yanel instance ... but Yanel should be a singleton, because it instantiates the Map ... see Cmdl and Servlet ...
            //resource.setYanel(...);

            return resource;
        } else {
            log.error("No resource registered for rti: " + universalName);
            return null;
        }
    }


    /**
     *
     */
    public String getConfigurationFile() {
        return CONFIGURATION_FILE;
    }
}
