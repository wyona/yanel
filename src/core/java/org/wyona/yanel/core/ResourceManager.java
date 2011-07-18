/*
 * Copyright 2006-2009 Wyona
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

import org.apache.log4j.Logger;

import org.wyona.yanel.core.api.ResourceTypeMatcherV1;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.util.HttpServletRequestHelper;
import org.wyona.yanel.core.util.PathUtil;

import org.wyona.yarep.core.NoSuchNodeException;

/**
 * Service responsible for instantiating {@link Resource} objects for requests
 * given some initial resource-type-related configuration
 * and some context for the request.
 */
public class ResourceManager {

    private static Logger log = Logger.getLogger(ResourceManager.class);
    
    protected ResourceTypeRegistry rtRegistry;
    private ResourceTypeMatcherV1 resourceTypeMatcher;

    /**
     * Set default resource type registry and resource type matcher
     */
    public ResourceManager() {
        rtRegistry = new ResourceTypeRegistry();
        resourceTypeMatcher = new ResourceTypeDefaultMatcher();
    }
    
    /**
     * Set resource type registry
     */
    public void setResourceTypeRegistry(ResourceTypeRegistry registry) {
        this.rtRegistry = registry;
    }

    // TODO: getResource(Environment, definition) What about the realm? Do we really need a path? TBD...

    /**
     * Creates a new resource object in the given realm with the given path and the given type.
     * @deprecated Use {@link #getResource(Environment, Realm, String, ResourceConfiguration)} instead,
     *  or if you cannot then maybe
     *  <code>getResource(environment, realm, path, new RTIbasedResourceConfiguration(rti))</code>
     *  will suit your situation better.
     *
     * @param environment Environment which for example contains request and response
     * @param realm Realm where resource is living
     * @param path Path relative to realm (e.g. yanel.getMap().getPath(realm, request.getServletPath()))
     * @param rtd Resource type definition, should be not <samp>null</samp>
     * @param rti Resource type identifier (deprecated and contains redundant information to resource type definition. What about properties?)
     */
    @Deprecated
    public Resource getResource(Environment environment, Realm realm, String path, ResourceTypeDefinition rtd, ResourceTypeIdentifier rti) throws Exception {
        return getResource(environment, realm, path, rtd, new RTIbasedResourceConfiguration(rti));
    }

    /**
     * Creates a new resource object in the given realm with the given path and the given type.
     *
     * @param path Path relative to realm (e.g. yanel.getMap().getPath(realm, request.getServletPath()))
     */
    public Resource getResource(Environment environment, Realm realm, String path, ResourceConfiguration rc) throws Exception {
        ResourceTypeDefinition rtd = rtRegistry.getResourceTypeDefinition(rc.getUniversalName());
        if (rtd != null) {
            return getResource(environment, realm, path, rtd, rc);
        }
        log.error("Resource Type Definition cannot be determined for: " + realm + ", " + path + ", " + rc.getUniversalName());
        return null;
    }

    /**
     * @param rtd not <samp>null</samp>
     */
    private Resource getResource(Environment environment, Realm realm, String path, ResourceTypeDefinition rtd, ResourceConfiguration rc) throws Exception {
            try {
                Resource resource = (Resource) Class.forName(rtd.getResourceTypeClassname()).newInstance();

                resource.setRTD(rtd);
                resource.setYanel(Yanel.getInstance());
                resource.setRealm(realm);
                resource.setPath(path);
                resource.setConfiguration(rc);

                // small hack to support old-style "RTI" resource-configuration objects:
                if (rc instanceof RTIbasedResourceConfiguration) {
                    resource.setRTI(((RTIbasedResourceConfiguration) rc).getRTI());
                }

                resource.setEnvironment(environment);
            
                passParameters(resource, environment.getRequest());
            
                return resource;
            } catch (ClassNotFoundException e) {
                log.error("Resource class not found for " + rtd.getResourceTypeUniversalName());
                throw e;
            }
    }

    /**
     * Creates a new resource object in the given realm and with the given path.
     *
     * @param environment Yanel environment containing request, response, etc.
     * @param realm Realm for which resource/controller should handle/process request
     * @param path Path starting at root of realm (e.g. yanel.getMap().getPath(realm, request.getServletPath()))
     */
    public Resource getResource(Environment environment, Realm realm, String path) throws Exception {
        ResourceConfiguration resConfig = resourceTypeMatcher.getResourceConfiguration(environment, realm, path);
        if (resConfig != null) {
            return getResource(environment, realm, path, resConfig);
        }
        
        //log.debug("Fallback to 'file' (aka 'node') resource/controller...");
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
     * File upload parameters of multipart requests won't be passed.
     * @param resource
     * @param request
     */
    protected void passParameters(Resource resource, HttpServletRequest request) {
        if (request != null) {
            Enumeration<?> paramNames = request.getParameterNames();
            
            while (paramNames.hasMoreElements()) {
                String name = (String)paramNames.nextElement();
                String[] values = request.getParameterValues(name);
                if (values != null) {
                    if (values.length == 1) {
                        resource.setParameter(name, HttpServletRequestHelper.getParameterValue(request, values[0]));
                    } else {
                        String[] stringValues = new String[values.length];
                        for (int i = 0; i < values.length; i++) {
                            stringValues[i] = HttpServletRequestHelper.getParameterValue(request, values[i]);
                        }
                        resource.setParameter(name, stringValues);
                    }
                } else {
                    log.error("No values for request parameter: " + name);
                }
            }
        }
    }
    
}
