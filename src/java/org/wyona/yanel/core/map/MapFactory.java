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

/**
 *
 */
public abstract class MapFactory {

    /**
     * Obtain a new instance of a MapFactory.
     * This method uses the following ordered lookup procedure
     * to determine the MapFactory implementation class to load:
     *
     * - Use the org.wyona.yanel.core.maps.MapFactory system property.
     * - Use the properties file "yanel.properties" in the classpath.
     *   This configuration file is in standard java.util.Properties format
     *   and contains the fully qualified name of the implementation class
     *   with the key being the system property defined above.
     * - Use the Services API (as detailed in the JAR specification), if available, to determine the classname.
     *   The Services API will look for a classname in the file
     *   META-INF/services/org.wyona.yanel.core.maps.MapFactory in jars available to the runtime.
     *
     * Once an application has obtained a reference to a MapFactory it can
     * use the factory to configure and obtain maps instances.
     */
    public static MapFactory newInstance() {
        try {
            return (MapFactory) Class.forName("org.wyona.yanel.impl.map.MapFactoryImpl").newInstance();
        } catch (Exception e) {
            System.err.println(e);
        }
        return null;
    }

    /**
     *
     */
    public abstract Map newMap();
}
