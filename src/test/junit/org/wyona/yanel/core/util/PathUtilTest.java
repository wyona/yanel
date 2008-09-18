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

import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.junit.AbstractYanelTest;

/**
 * Simple test for the map implementation.
 */
public class PathUtilTest extends AbstractYanelTest {


    public void setUp() throws Exception {
        super.setUp();
        this.testName = "Test for the PathUtil";
    }

    /**
     * Tests if the PathUtil.getRTIPath returns the correct RTIPath for a given path.
     */
    public void testRTIPath() throws Exception {
        String path = "/yanel-website/en/about.html";
        String RTIPath = PathUtil.getRTIPath(path);
        assertEquals("Incorrect RTIPath for path: " + path.toString(), "/yanel-website/en/about.html.yanel-rti", RTIPath);
        
        path = "/";
        RTIPath = PathUtil.getRTIPath(path);
        assertEquals("Incorrect RTIPath for path: " + path.toString(), "/.yanel-rti", RTIPath);

        path = "";
        RTIPath = PathUtil.getRTIPath(path);
        assertEquals("Incorrect RTIPath for path: " + path.toString(), ".yanel-rti", RTIPath);
    }

    /**
     * Tests if the PathUtil.getRCPath returns the correct RCPath for a given path.
     */
    public void testRCPath() throws Exception {
        String path = "/en/about.html";
        String RCPath = PathUtil.getRCPath(path);
        assertEquals("Incorrect RCPath for path: " + path.toString(), "/en/about.html.yanel-rc", RCPath);
        
        path = "/";
        RCPath = PathUtil.getRCPath(path);
        assertEquals("Incorrect RCPath for path: " + path.toString(), "/.yanel-rc", RCPath);

        path = "";
        RCPath = PathUtil.getRCPath(path);
        assertEquals("Incorrect RCPath for path: " + path.toString(), ".yanel-rc", RCPath);
    }
    
    
    /**
     * Tests if the PathUtil.backToContext returns the correct amount of "../" for a given path.
     */
    public void testBackToContext() throws Exception {
        Realm realm = new Realm("test", "test", "/", null);
        String backToContextPath = PathUtil.backToContext(realm);
        assertEquals("Incorrect backToContextPath for realm mount point: " + realm.getMountPoint(), "", backToContextPath);
        
        realm = new Realm("test", "test", "/test/usecase/", null);
        backToContextPath = PathUtil.backToContext(realm);
        assertEquals("Incorrect backToContextPath for realm mount point: " + realm.getMountPoint(), "../../", backToContextPath);
        
        realm = new Realm("test", "test", "/yanel-website/", null);
        backToContextPath = PathUtil.backToContext(realm);
        assertEquals("Incorrect backToContextPath for realm mount point: " + realm.getMountPoint(), "../", backToContextPath);
        
    }
    
    /**
     * Tests if the map returns the correct rti for a given path.
     */
    public void testBackToRealm() throws Exception {
        String path = "/en/about.html";
        String backToRealmPath = PathUtil.backToRealm(path);
        assertEquals("Incorrect backToRealmPath for path: " + path.toString(), "../", backToRealmPath);

        path = "/en/";
        backToRealmPath = PathUtil.backToRealm(path);
        assertEquals("Incorrect backToRealmPath for path: " + path.toString(), "../", backToRealmPath);

        path = "/";
        backToRealmPath = PathUtil.backToRealm(path);
        assertEquals("Incorrect backToRealmPath for path: " + path.toString(), "", backToRealmPath);
    }
}
