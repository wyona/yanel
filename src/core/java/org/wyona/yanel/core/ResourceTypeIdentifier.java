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

import java.io.BufferedReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Category;

/**
 * Abstraction of an rti file.
 * Convention:
 * - first line contains resource type identifier string: <{http://www.wyona.org/yanel/resource/1.0}file/>
 * - all remaining lines contain properties in the following format:
 *        key:value
 * - lines starting with '#' are ignored
 * - lines which don't contain a ':' are ignored
 *        
 * TODO: this class should be renamed
 */
public class ResourceTypeIdentifier {

    private Category log = Category.getInstance(ResourceTypeDefinition.class);

    protected Map properties;
    protected String universalName;
    
    public ResourceTypeIdentifier(Reader reader) throws Exception {
        BufferedReader br = new BufferedReader(reader);

        this.universalName = br.readLine();
        this.properties = new HashMap();
        
        String property;
        while ((property = br.readLine()) != null) {
            int colonIndex = property.indexOf(":");
            if (colonIndex > 0 && !property.trim().startsWith("#")) {
                String name = property.substring(0, colonIndex).trim();
                String value = property.substring(colonIndex + 1).trim();
                this.properties.put(name, value);
            }
        }
    }
    
    public ResourceTypeIdentifier(String universalName, Map properties) {
        if (properties == null) {
            this.properties = new HashMap();
        } else {
            this.properties = properties;
        }
        this.universalName = universalName;
    }
    
    public String getUniversalName() {
        return universalName;
    }
    
    /**
     * @param key
     * @return value for this key or null if no value exists for this key.
     */
    public String getProperty(String key) {
        return (String)properties.get(key);
    }
    
    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }


}
