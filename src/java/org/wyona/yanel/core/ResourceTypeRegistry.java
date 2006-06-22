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

import java.lang.ClassNotFoundException;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Category;

/**
 *
 */
public class ResourceTypeRegistry {

    private static Category log = Category.getInstance(ResourceTypeRegistry.class);

    public static final String DEFAULT_CONFIGURATION_FILE = "yanel.properties";
    public static String CONFIGURATION_FILE = DEFAULT_CONFIGURATION_FILE;

    private URL propertiesURL;

    /**
     *
     */
    public ResourceTypeRegistry() {
        this(DEFAULT_CONFIGURATION_FILE);
    }

    /**
     *
     */
    public ResourceTypeRegistry(String configurationFile) {
        CONFIGURATION_FILE = configurationFile;

        propertiesURL = ResourceTypeRegistry.class.getClassLoader().getResource(CONFIGURATION_FILE);
        if (propertiesURL == null) {
            log.error("No such resource: " + CONFIGURATION_FILE);
            return;
        }

        Properties props = new Properties();
        try {
            props.load(propertiesURL.openStream());
            String separator = ",";
            String[] tokens = props.getProperty("resources").split(separator);
            for (int i = 0; i < tokens.length; i++) {
                log.debug("Resource descriptor: " + tokens[i]);
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     *
     */
    public static ResourceTypeDefinition getResourceTypeDefinition(String universalName) {
        return new ResourceTypeDefinition(universalName);
    }

    /**
     *
     */
    public static Resource newResource(String universalName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        // TODO: Read from configuration
        java.util.HashMap hm = new java.util.HashMap(); 
        hm.put("<{http://www.wyona.org/yanel/resource/1.0}file/>", "org.wyona.yanel.impl.resources.FileResource");
        hm.put("<{http://www.wyona.org/yanel/resource/1.0}directory/>", "org.wyona.yanel.impl.resources.DirectoryResource");
        hm.put("<{http://www.wyona.org/yanel/resource/1.0}default/>", "org.wyona.yanel.impl.ResourceDefaultImpl");
        hm.put("<{http://www.wyonapictures.com/yanel/resource/1.0}tape/>", "com.wyonapictures.yanel.impl.resources.TapeResource");
        hm.put("<{http://www.wyona.com/yanel/resource/1.0}invoice/>", "com.wyona.yanel.impl.resources.InvoiceResource");
        hm.put("<{http://www.wyona.org/yanel/resource/1.0}websearch/>", "org.wyona.yanel.impl.resources.WebSearchResource");

	String className = (String) hm.get(universalName);
        if (className != null) {
            return (Resource) Class.forName(className).newInstance();
        } else {
            log.error("No resource registered for rti: " + universalName);
            return null;
        }
    }
}
