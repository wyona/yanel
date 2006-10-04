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

import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.MapFactory;

/**
 *
 */
public class Yanel {

    Map map = null;
    ResourceTypeRegistry rtr = null;

    /**
     *
     */
    public Yanel() {
        MapFactory mf = MapFactory.newInstance();
        map = mf.newMap();
        rtr = new ResourceTypeRegistry();
    }

    /**
     *
     */
    public Resource getResource(Path path) throws Exception {
        String rti = map.getResourceTypeIdentifier(path);
        Resource resource = rtr.newResource(rti);

        ResourceTypeDefinition rtd = rtr.getResourceTypeDefinition(rti);
        resource.setRTD(rtd);

        resource.setYanel(this);

        return resource;
    }
}
