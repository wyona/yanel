/*
 * Copyright 2011 Wyona
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

import org.apache.log4j.Logger;

import org.wyona.yanel.core.api.ResourceTypeMatcherV1;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.util.PathUtil;

import org.wyona.yarep.core.NoSuchNodeException;

import java.io.Reader;

/**
 * Request matcher implementation
 */
public class ResourceTypeMatcherV1ImplV2 implements ResourceTypeMatcherV1 {

    private static Logger log = Logger.getLogger(ResourceTypeMatcherV1ImplV2.class);
    
    /**
     * Get resource configuration for a given realm and for a given path.
     * The chain of responsibility is as follows:
     *  1) One-to-one mapping (.yanel-rc and then deprecated .yanel-rti)
     *  2) RC map
     *  3) file/node resource type
     *
     * @param environment Yanel environment containing request, response, etc.
     * @param realm Realm for which resource/controller should handle/process request
     * @param path Path starting at root of realm (e.g. yanel.getMap().getPath(realm, request.getServletPath()))
     */
    public ResourceConfiguration getResourceConfiguration(Environment environment, Realm realm, String path) throws Exception {
        // Check first if a one-to-one mapping exists
        if (realm.getRTIRepository().existsNode(PathUtil.getRCPath(path))) {
            ResourceConfiguration rc = new ResourceConfiguration(realm.getRTIRepository().getNode(PathUtil.getRCPath(path)));
            if (rc != null) return rc;
        }

        // Fallback to deprecated RTI (one-to-one mapping)
        if (realm.getRTIRepository().existsNode(PathUtil.getRTIPath(path))) {
            log.warn("DEPRECATED: RTI should be replaced by ResourceConfiguration: " + realm + ", " + path);
            ResourceTypeIdentifier rti = getResourceTypeIdentifier(realm, path);
            ResourceConfiguration rc = new RTIbasedResourceConfiguration(rti);
            return rc;
        } 

        // Check on resource configuration map
        String rcPath = ResourceConfigurationMapV2.getRCPath(realm, path, environment.getRequest());
        if (rcPath != null) {
            if (realm.getRTIRepository().existsNode(rcPath)) {
                ResourceConfiguration rc = new ResourceConfiguration(realm.getRTIRepository().getNode(rcPath));
                if (rc != null) return rc;
            } else {
                throw new Exception("Request did match within resource configuration map of realm '" + realm.getName() + "', but no such resource type configuration node exist: " + rcPath);
            }
        } 
        
        //log.debug("No resource configuration for request: " + path + ", " + realm.getID());
        return null;
    }

    /**
     * Returns the abstraction of the rti file for the given path in the realm.
     * TODO: move this method to some RTIManager class ?
     * @deprecated
     */
    private ResourceTypeIdentifier getResourceTypeIdentifier(Realm realm, String path) throws Exception {
        log.debug("Original path: " + path);
        try {
            Reader reader = realm.getRTIRepository().getReader(new Path(PathUtil.getRTIPath(path)));
            return new ResourceTypeIdentifier(reader);
        } catch(NoSuchNodeException e) {
            log.warn(e.getMessage());
            log.warn("TODO: Implement chain of responsibility ...");
            return new ResourceTypeIdentifier("<{http://www.wyona.org/yanel/resource/1.0}file/>", null);
        }
    }
}
