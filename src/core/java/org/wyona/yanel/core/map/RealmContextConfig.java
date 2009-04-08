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
 * Class providing access to realm configuration
 */
public class RealmContextConfig {

    private static Logger log = Logger.getLogger(RealmContextConfig.class);

    private String id;
    private String mountPoint;
    private boolean rootFlag;
    private File realmConfigFile;

    public RealmContextConfig(String id, String mountPoint, boolean rootFlag, File realmConfigFile) {
        this.id = id;
        this.mountPoint = mountPoint;
        this.rootFlag = rootFlag;
        this.realmConfigFile = realmConfigFile;
    }

    /**
     * Get realm configuration file
     */
    public File getConfigurationFile() {
        return realmConfigFile;
    }

    /**
     *
     */
    public String toString() {
        String s = "Realm context configuration:\n";
        s = s + "  ID = " + id;
        s = s + "  Mount point = " + mountPoint;
        s = s + "  Root flag = " + rootFlag;
        s = s + "  Realm config file = " + realmConfigFile;
        return s;
    }
}
