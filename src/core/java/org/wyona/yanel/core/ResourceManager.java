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

import java.io.Reader;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Category;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.util.HttpServletRequestHelper;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yarep.core.NoSuchNodeException;

/**
 *
 */
public class ResourceManager {

    private static Category log = Category.getInstance(ResourceManager.class);
    
    protected ResourceTypeRegistry rtRegistry;
    
    /**
     *
     */
    public ResourceManager() {
    }
    
    public void setResourceTypeRegistry(ResourceTypeRegistry registry) {
        this.rtRegistry = registry;
    }

    /**
     * Creates a new resource object in the given realm with the given path and the given type.
     *
     * @param path Path relative to realm (e.g. yanel.getMap().getPath(realm, request.getServletPath()))
     */
    public Resource getResource(Environment environment, Realm realm, String path, 
            ResourceTypeDefinition rtd, ResourceTypeIdentifier rti) 
    throws Exception {
        String universalName = rtd.getResourceTypeUniversalName();
        if (rtd != null) {
            Resource resource = (Resource) Class.forName(rtd.getResourceTypeClassname()).newInstance();

            resource.setRTD(rtd);
            resource.setYanel(Yanel.getInstance());
            resource.setRealm(realm);
            resource.setPath(path);
            resource.setRTI(rti);
            resource.setEnvironment(environment);
            
            passParameters(resource, environment.getRequest());
            
            return resource;
        } else {
            log.error("No resource registered for rti: " + universalName);
            return null;
        }
    }

    /**
     * Creates a new resource object in the given realm with the given path and the given type.
     *
     * @param path Path relative to realm (e.g. yanel.getMap().getPath(realm, request.getServletPath()))
     */
    public Resource getResource(Environment environment, Realm realm, String path, ResourceConfiguration rc) throws Exception {
        ResourceTypeDefinition rtd = rtRegistry.getResourceTypeDefinition(rc.getUniversalName());

        if (rtd != null) {
            String universalName = rtd.getResourceTypeUniversalName();
            Resource resource = (Resource) Class.forName(rtd.getResourceTypeClassname()).newInstance();

            resource.setRTD(rtd);
            resource.setYanel(Yanel.getInstance());
            resource.setRealm(realm);
            resource.setPath(path);
            resource.setConfiguration(rc);
            resource.setEnvironment(environment);
            
            passParameters(resource, environment.getRequest());
            
            return resource;
        } else {
            log.error("Resource Type Definition cannot be determined for: " + realm + ", " + path + ", " + rc.getUniversalName());
            return null;
        }
    }

    /**
     * Creates a new resource object in the given realm and with the given path.
     *
     * @param path Path relative to realm (e.g. yanel.getMap().getPath(realm, request.getServletPath()))
     */
    public Resource getResource(Environment environment, Realm realm, String path) throws Exception {
        if (realm.getRTIRepository().exists(new Path(PathUtil.getRCPath(path)))) {
            ResourceConfiguration rc = new ResourceConfiguration(realm.getRTIRepository().getInputStream(new Path(PathUtil.getRCPath(path))));
            if (rc != null) return getResource(environment, realm, path, rc);
        }

        if (realm.getRTIRepository().exists(new Path(PathUtil.getRTIPath(path)))) {
            // Fallback to deprecated RTI
            log.warn("DEPRECATED: RTI should be replaced by ResourceConfiguration: " + realm + ", " + path);
            ResourceTypeIdentifier rti = getResourceTypeIdentifier(realm, path);
            ResourceTypeDefinition rtd = rtRegistry.getResourceTypeDefinition(rti.getUniversalName());
            return getResource(environment, realm, path, rtd, rti);
        } 

        String rcPath = ResourceConfigurationMap.getRCPath(realm, path);
        if (rcPath != null && realm.getRTIRepository().exists(new Path(rcPath))) {
            ResourceConfiguration rc = new ResourceConfiguration(realm.getRTIRepository().getInputStream(new Path(ResourceConfigurationMap.getRCPath(realm, path))));
            if (rc != null) return getResource(environment, realm, path, rc);
        } 
        
        return getResource(environment, realm, path, new ResourceConfiguration("file", "http://www.wyona.org/yanel/resource/1.0", null));
        
    }

    /**
     * Returns the abstraction of the rti file for the given path in the realm.
     * TODO: move this method to some RTIManager class ?
     * @deprecated
     */
    public ResourceTypeIdentifier getResourceTypeIdentifier(Realm realm, String path) throws Exception {
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
    
    /**
     * Passes request parameter to a resource.
     * String parameters will be decoded.
     * TODO: handle multipart requests.
     * @param resource
     * @param request
     */
    protected void passParameters(Resource resource, HttpServletRequest request) {
        if (request != null) {
            Enumeration paramNames = request.getParameterNames();
            
            while (paramNames.hasMoreElements()) {
                String name = (String)paramNames.nextElement();
                String[] values = request.getParameterValues(name);
                if (values.length == 1) {
                    resource.setParameter(name, HttpServletRequestHelper.decode(values[0]));
                } else {
                    String[] stringValues = new String[values.length];
                    for (int i = 0; i < values.length; i++) {
                        stringValues[i] = HttpServletRequestHelper.decode(values[i]);
                    }
                    resource.setParameter(name, stringValues);
                }
            }
        }
    }
    
}
