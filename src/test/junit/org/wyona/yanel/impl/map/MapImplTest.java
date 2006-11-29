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
package org.wyona.yanel.impl.map;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.junit.AbstractYanelTest;

/**
 * Simple test for the map implementation.
 */
public class MapImplTest extends AbstractYanelTest {

    protected Map map;

    public void setUp() throws Exception {
        super.setUp();
        this.testName = "Test for the Map Implementation";
        this.map = new MapImpl();
    }

    /**
     * Tests if the map returns the correct rti for a given path.
     */
    public void testRTI() throws Exception {
        Path path = new Path("/yanel-website/en/about.html");
        String rti = map.getResourceTypeIdentifier(path);
        assertEquals("Incorrect RTI for path: " + path.toString(),
                "<{http://www.wyona.org/yanel/resource/1.0}xml/>", rti);
    }

}
