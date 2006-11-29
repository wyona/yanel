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
package org.wyona.yanel.htmlunit.yanelwebsite;

import org.wyona.yanel.htmlunit.AbstractHtmlUnitTest;

/**
 * Simple test which loads a yanel page and verifies the title.
 */
public class SimpleWebTest extends AbstractHtmlUnitTest {

    protected void setUp() throws Exception {
        this.testName = "Simple Yanel Web Test";
        super.setUp();
    }

    /**
     * Loads a yanel page and verifies its title.
     */
    public void testYanelWebsite() throws Exception {
        loadHtmlPage(""); // root page
        assertTitleEquals("Welcome to Yanel");
        assertPageContainsText("Welcome to Yanel");
    }

}
