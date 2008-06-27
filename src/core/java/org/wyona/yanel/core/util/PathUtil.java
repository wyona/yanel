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

package org.wyona.yanel.core.util;

import org.apache.log4j.Category;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.Resource;

/**
 *
 */
public class PathUtil extends org.wyona.commons.io.PathUtil {

    private static Category log = Category.getInstance(PathUtil.class);

    /**
     *
     */
    public static String getRTIPath(String path) {
       // Remove trailing slash except for ROOT ...
       if (path.length() > 1 && path.charAt(path.length() - 1) == '/') {
           return path.substring(0, path.length() - 1) + ".yanel-rti";
       }
       return path + ".yanel-rti";
   }

   /**
    *
    */
   public static String getRCPath(String path) {
       // Remove trailing slash except for ROOT ...
       if (path.length() > 1 && path.charAt(path.length() - 1) == '/') {
           return path.substring(0, path.length() - 1) + ".yanel-rc";
       }
       return path + ".yanel-rc";
   }
   
   /**
    * @return a String with as many ../ as it needs to go back to from current realm to context
    */
   public static String backToContext(Realm realm) {
       StringBuffer backToContext = new StringBuffer();
       int steps = realm.getMountPoint().split("/").length - 1;
       for (int i = 0; i < steps; i++) {
           backToContext.append("../");
       }
       return backToContext.toString();
   }
   
   /**
    * @param realm Realm
    * @param path Path relative to realm
    * @return a String with as many ../ as it needs to go back to from current resource to context
    */
   public static String backToContext(Realm realm, String path) {
       return backToContext(realm) + backToRealm(path);
   }
   
   /**
    * @param path Path relative to realm
    * @return a String with as many ../ as it needs to go back to from current resource to the realm-root
    */
   public static String backToRealm(String path) {
       StringBuffer backToRealm = new StringBuffer();
       int steps;
       if (path.endsWith("/") && !path.equals("/")) {
           steps =  path.split("/").length - 1;
       } else {
           steps =  path.split("/").length - 2;
       }
       if (steps == 0) return "./";
       for (int i = 0; i < steps; i++) {
           backToRealm.append("../");
       }
       return backToRealm.toString();
   }
   
    /**
     * @param resource Resource
     * @return A string with as many ../ as it needs to get from the resource path to the root of the realm and adds the reservedPrefix and resource-types/resource-type-namespace::resource-type-localname/ to it. 
     * @deprecated use getResourcesHtdocsPathURLencoded(Resource) instead.
     */
    public static String getResourcesHtdocsPath(Resource resource) {
        return getGlobalHtdocsPath(resource) + "resource-types/" + resource.getResourceTypeNamespace() + "::" + resource.getResourceTypeLocalName() + "/";
    }

    /**
     * @param resource Resource
     * @return A string with as many ../ as it needs to get from the resource path to the root of the realm and adds the reservedPrefix and resource-types/resource-type-namespace%3a%3aresource-type-localname/ to it, whereas the resource-type-universalname is url-encoded.
     */
    public static String getResourcesHtdocsPathURLencoded(Resource resource) {
        return getGlobalHtdocsPath(resource) + "resource-types/" + resource.getResourceTypeNamespace().replaceAll("/", "%2f").replaceAll(":", "%3a") + "%3a%3a" + resource.getResourceTypeLocalName() + "/";
    }
    
    /**
     * @param resource Resource
     * @return A string with as many ../ as it needs to get from the resource path to the root of the realm and adds the reservedPrefix to it. 
     */
    public static String getGlobalHtdocsPath(Resource resource) {
        return backToRealm(resource.getPath()) + resource.getYanel().getReservedPrefix() + "/";
    }
}
