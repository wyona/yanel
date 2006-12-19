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

import org.apache.log4j.Category;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

/**
 *
 */
public class ResourceTypeDefinition {

    private Category log = Category.getInstance(ResourceTypeDefinition.class);

    private String uname;
    private String classname;
    private String description;

    private File configFile;

    /**
     *
     */
    public ResourceTypeDefinition(File file) {
        this.configFile = file;
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration config;
        try {
            config = builder.buildFromFile(file);
            String localName = config.getAttribute("name", null);
            String namespace= config.getAttribute("namespace", null);
            uname = "<{" + namespace + "}" + localName + "/>";
            classname = config.getAttribute("class", null);
            description = config.getChild("description").getValue();
        } catch(Exception e) {
            log.error(e);
        }
    }

    /**
     *
     */
    public String getResourceTypeUniversalName() {
        return uname;
    }

    /**
     *
     */
    public String getResourceTypeLocalName() {
        return uname.substring(uname.indexOf("}") + 1, uname.length() -2);
    }

    /**
     *
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
        return configFile;
    }
}
