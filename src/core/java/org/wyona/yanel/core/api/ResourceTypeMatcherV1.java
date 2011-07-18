/*
 * Copyright 2011 Wyona
 */
package org.wyona.yanel.core.api;

import org.wyona.yanel.core.Environment;
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.map.Realm;

/**
 * Resource type matcher interface (version 1) in order to make resource loading more flexible
 */
public interface ResourceTypeMatcherV1 {

    /**
     * Get resource configuration for a particular path for a particular realm
     * The chain of responsibility is as follows:
     *  1) One-to-one mapping (.yanel-rc and then deprecated .yanel-rti)
     *  2) RC map
     *  3) file/node resource type
     *
     * @param environment Yanel environment containing request, response, etc.
     * @param realm Realm for which resource/controller should handle/process request
     * @param path Path starting at root of realm (e.g. yanel.getMap().getPath(realm, request.getServletPath()))
     */
    public ResourceConfiguration getResourceConfiguration(Environment environment, Realm realm, String path) throws Exception;
}
