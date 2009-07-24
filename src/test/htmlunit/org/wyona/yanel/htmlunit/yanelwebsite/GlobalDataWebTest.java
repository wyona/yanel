/*
 * Copyright 2008, 2009 Wyona
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
 * Test the resources under the Yanel reserved prefix.
 */
public class GlobalDataWebTest extends AbstractHtmlUnitTest {

    private String getReservedURL(String resourceRelativeURL) throws Exception {
        return reservedPrefix  + resourceRelativeURL;
    }

    private void loadReservedHtmlPage(String pageRelativeURL) throws Exception {
        loadHtmlPage(getReservedURL(pageRelativeURL));
    }

    private void loadReservedErrorPage(String pageRelativeURL, int expectedErrorCode) throws Exception {
        loadErrorPage(getReservedURL(pageRelativeURL), expectedErrorCode);
    }

    private void loadReservedResource(String resourceRelativeURL) throws Exception {
        loadResource(getReservedURL(resourceRelativeURL));
    }

    protected void setUp() throws Exception {
        this.testName = "Global Data Web Test";
        super.setUp();
    }

    public void testAboutPage() throws Exception {
        loadReservedHtmlPage("about.html");
        assertTitleEquals("About Yanel");
        assertPageContainsText("Version");
    }

    public void testUsersPages() throws Exception {
        //TODO: test an existing user page

        //XXX: IMHO ( see http://lists.wyona.org/pipermail/yanel-development/2008-December/002514.html ) this should instead redirect to a login page or at least fail gracefully:
        loadReservedErrorPage("users/dummy.html", 500);

        loadReservedErrorPage("user-mgmt/list-users.html", 404);
    }

    public void testDataRepoSitetreePage() throws Exception {
        loadReservedHtmlPage("data-repository-sitetree.html");
        assertTitleContains("Sitetree");
        assertTitleContains("Yanel");
        //XXX should work, why does it not?!?: clickLink("?yanel.resource.viewid=xmlVersion");
    }

    public void testResourceTypesPages() throws Exception {
        loadReservedErrorPage("resource-types/dummy", /* XXX: should be 404: */500);

        loadReservedErrorPage("resource-types/http%3a%2f%2fwww.wyona.org%2fyanel%2fresource%2f1.0%3a%3afile/"+reservedPrefix+"icons/1x1/rt-icon.png", /* XXX: should be 404: */500);
        loadReservedErrorPage("resource-types/^http^3a^2f^2fwww.wyona.org^2fyanel^2fresource^2f1.0::file/"+reservedPrefix+"icons/1x1/rt-icon.png", /* XXX: should be 404: */500);
        loadReservedErrorPage("resource-types/http://www.wyona.org/yanel/resource/1.0::file/"+reservedPrefix+"icons/1x1/rt-icon.png", /* XXX: should be 404: */500);

        loadReservedResource("resource-types/http%3a%2f%2fwww.wyona.org%2fyanel%2fresource%2f1.0%3a%3afile/"+reservedPrefix+"icons/32x32/rt-icon.png");
        loadReservedResource("resource-types/^http^3a^2f^2fwww.wyona.org^2fyanel^2fresource^2f1.0::file/"+reservedPrefix+"icons/32x32/rt-icon.png");
        loadReservedResource("resource-types/http://www.wyona.org/yanel/resource/1.0::file/"+reservedPrefix+"icons/32x32/rt-icon.png");
        assertNotNull(response.getResponseHeaderValue("Last-Modified"));
        //TODO: test 304 handling

        loadReservedErrorPage("resource-types/http%3a%2f%2fwww.wyona.org%2fyanel%2fresource%2f1.0%3a%3atesting-control/dummy", /* XXX: should be 404: */500);
        loadReservedErrorPage("resource-types/^http^3a^2f^2fwww.wyona.org^2fyanel^2fresource^2f1.0::testing-control/dummy", /* XXX: should be 404: */500);
        loadReservedErrorPage("resource-types/http://www.wyona.org/yanel/resource/1.0::testing-control/dummy", /* XXX: should be 404: */500);

        loadReservedResource("resource-types/http%3a%2f%2fwww.wyona.org%2fyanel%2fresource%2f1.0%3a%3atesting-control/js/ajaxexecutetests.js");
        loadReservedResource("resource-types/^http^3a^2f^2fwww.wyona.org^2fyanel^2fresource^2f1.0::testing-control/js/ajaxexecutetests.js");
        loadReservedResource("resource-types/http://www.wyona.org/yanel/resource/1.0::testing-control/js/ajaxexecutetests.js");
        assertNotNull(response.getResponseHeaderValue("Last-Modified"));
        //TODO: test 304 handling
    }

    /**
     * Test globally available htdocs (located within src/webapp/htdocs/ or rather local/apache-tomcat-5.5.20/webapps/yanel/htdocs/)
     */
    public void testHtdocsPages() throws Exception {
        loadReservedHtmlPage("help.html");
        // TODO: should be not null?!
        assertNull(response.getResponseHeaderValue("Last-Modified"));
        assertTitleContains("Help");

        loadReservedErrorPage("../index.html?yanel.toolbar=on", 401);
        //clickLink("../index.html?yanel.toolbar=on");
        //assertTitleContains("Login");

        loadReservedResource("yanel_toolbar_logo.png");
    }

    public void testNonExistingPage() throws Exception {
        loadReservedErrorPage("dummy.html", 404);
    }

}
