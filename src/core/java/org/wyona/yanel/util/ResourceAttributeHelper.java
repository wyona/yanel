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

package org.wyona.yanel.util;

import org.wyona.yanel.core.Resource;

import org.apache.log4j.Category;

/**
 * @deprecated Use org.wyona.yanel.core.util.ResourceAttributeHelper
 */
public class ResourceAttributeHelper {

    private static Category log = Category.getInstance(ResourceAttributeHelper.class);

    /**
     *
     */
    static public boolean hasAttributeImplemented(Resource res, String attribute, String version) {
        boolean implemented = false;
        Class clazz = res.getClass();
        
        while (!clazz.getName().equals("java.lang.Object") && !implemented) {
            Class[] interfaces = clazz.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                
                if (interfaces[i].getName().equals("org.wyona.yanel.core.api.attributes." + attribute + "V" + version)) {
                    implemented = true;
                    break;
                }
                // TODO: Why does this not work?
                //if (interfaces[i].isInstance(iface)) implemented = true;
            }
            clazz = clazz.getSuperclass();
        }
        if (implemented) {
            log.info(res.getClass().getName() + " does implement " + attribute + "V" + version + " interface!");
        } else {
            log.info(res.getClass().getName() + " does NOT implement " + attribute + "V" + version + " interface!");
        }
        return implemented;
    }
}
