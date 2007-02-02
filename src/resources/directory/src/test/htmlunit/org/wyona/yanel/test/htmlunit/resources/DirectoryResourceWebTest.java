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
package org.wyona.yanel.test.htmlunit.resources;

import org.wyona.yanel.htmlunit.AbstractHtmlUnitTest;

/**
 * Test Directory Resource
 */
public class DirectoryResourceWebTest extends AbstractHtmlUnitTest {

    /**
     * Setup of test
     */
    protected void setUp() throws Exception {
        this.testName = "Directory Resource Web Test";
        super.setUp();
    }

    /**
     * Load an XHTML page and verifies its title and content.
     */
    public void testXHTMLViewOfDirectoryResource() throws Exception {
        loadHtmlPage("test/use-cases/hello/");
        assertTitleEquals("Collection: /hello/ - Yanel");
        assertPageContainsText("odt-sample.xhtml");
    }
}
