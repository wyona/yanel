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
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceDefaultImpl;
import org.wyona.yanel.core.ResourceTypeDefinition;
import org.wyona.yanel.core.ResourceTypeRegistry;
import org.wyona.yanel.core.ResourceDefaultImpl;
import org.wyona.yanel.core.attributes.CreatableResource;
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
        Path path = new Path("/hello/world.html");
        String rti = map.getResourceTypeIdentifier(path);
        System.out.println("Resource Type Identifier: " + rti);

        ResourceTypeDefinition rtd = ResourceTypeRegistry.getResourceTypeDefinition(rti);
        System.out.println("Local name: " + rtd.getResourceTypeLocalName());
        System.out.println("Namespace: " + rtd.getResourceTypeNamespace());

        Resource res = new ResourceDefaultImpl(rtd);
        Class[] interfaces = res.getClass().getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            System.out.println(interfaces[i].getName());
            //if (interfaces[i].isInstance(CreatableResource.class)) {
            if (1 > 0) {
                System.out.println(((CreatableResource) res).getPropertyNames());
            }
        }
        //System.out.println(res.getClass().getName() + " does not implement creatable interface!");
    }
}
