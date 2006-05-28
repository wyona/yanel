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

package org.wyona.yanel.cmdl;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.ResourceType;
import org.wyona.yanel.core.ResourceTypeRegistry;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.MapFactory;

/**
 *
 */
public class YanelCommandLine {

    /**
     *
     */
    static public void main(String[] args) {
        System.out.println("Hello Yanel!");

        MapFactory mf = MapFactory.newInstance();
        Map map = mf.newMap();
        String rti = map.getResourceTypeIdentifier(new Path("/hello/world.html"));
        System.out.println("Resource Type Identifier: " + rti);

        ResourceType rt = ResourceTypeRegistry.getResourceType(rti);
        System.out.println("Local name: " + rt.getLocalName());
        System.out.println("Namespace: " + rt.getNamespace());
    }
}
