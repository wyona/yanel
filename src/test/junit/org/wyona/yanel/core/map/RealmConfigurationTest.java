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
package org.wyona.yanel.core.map;

import org.wyona.yanel.junit.AbstractYanelTest;

/**
 * Simple test for the realm configuration
 */
public class RealmConfigurationTest extends AbstractYanelTest {

    protected RealmManager realmConfig;

    public void setUp() throws Exception {
        super.setUp();
        this.testName = "Test for the RealmManager";
        this.realmConfig = yanel.getRealmConfiguration();
    }

    /**
     * Tests
     */
    public void testCopyRealm() throws Exception {
        String testRealmID = "test-copy-realm-" + new java.util.Date().getTime();
        this.realmConfig.copyRealm("from-scratch-realm-template", testRealmID, "Test Realm (created by a Testcase)", "/test/" + testRealmID + "/", null);
        assertNotNull(this.realmConfig.getRealm(testRealmID));
    }

}
