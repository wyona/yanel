/*
 * Copyright 2005 The Apache Software Foundation Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wyona.yanel.htmlunit;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SimpleLog;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ThreadedRefreshHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.ClickableElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

/**
 * Base class to run HtmlUnit tests with JUnit.
 * 
 * Example:
 * 
 * <pre>
 * public void testHomePage() throws Exception {
 * 
 *     loadHtmlPage(&quot;index.html&quot;); // base url set in properties.xml
 *     assertTitleEquals(&quot;Welcome To The Index Page&quot;);
 * 
 *     clickLinkWithText(&quot;Contact&quot;);
 *     assertTitleContains(&quot;Contact&quot;);
 * }
 * 
 * </pre>
 * 
 * <p>
 * You also have full access to the htmlunit features (eg, the current page is
 * stored in the variable currentPage).
 * </p>
 * 
 * <pre>
 * public void testHomePage() throws Exception {
 *     final WebClient webClient = new WebClient();
 *     final URL url = new URL(&quot;http://localhost:8080/index.html&quot;);
 *     final HtmlPage page = (HtmlPage) webClient.getPage(url);
 *     assertEquals(&quot;Welcome To The Index Page&quot;, page.getTitleText());
 * }
 * </pre>
 */
public abstract class AbstractHtmlUnitTest extends TestCase {

    /** configuration */
    protected Configuration config;

    /**
     * Logger for informative output by test cases. The output level can be
     * specified in properties.xml
     */
    protected Log logger;

    /**
     * Base URL of the running Cocoon server which is to be tested. Set by
     * property htmlunit.test.baseurl.
     */
    protected String baseURL;

    /**
     * Low-level access to WebClient object.
     */
    protected WebClient webClient;

    /**
     * Low-level access to WebResponse object.
     */
    protected WebResponse response;

    /**
     * Holds the current HtmlPage which was loaded e.g. with loadHtmlPage().
     */
    protected HtmlPage currentPage;

    /**
     * The name of the testcase, used for debug output.
     */
    protected String testName;

    protected File baseDir;

    protected List collectedAlerts = new ArrayList();

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        // read the config file
        URL configURL = this.getClass().getClassLoader().getResource("htmlunit-properties.xml");
        config = new XMLConfiguration(configURL);

        SimpleLog log = new SimpleLog("htmlunit log");
        log.setLevel(Integer.parseInt(this.config.getString("htmlunit.debugLevel")));
        this.logger = log;
        this.baseURL = this.config.getString("htmlunit.baseUrl");
        // this.webClient = new WebClient(BrowserVersion.MOZILLA_1_0);
        // this.webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_6_0);
        this.webClient = new WebClient();
        this.webClient.addRequestHeader("Accept-Language", this.config
                .getString("htmlunit.language"));
        this.webClient.setRedirectEnabled(true);
        this.webClient.setAlertHandler(new CollectingAlertHandler(this.collectedAlerts));

        // disable javascript because it lead to problems with complex scripts.
        this.webClient.setJavaScriptEnabled(false);
        this.webClient.setRefreshHandler(new ThreadedRefreshHandler());
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#runTest()
     */
    protected void runTest() throws Throwable {
        this.logger.info("-------- Begin testcase [" + this.testName + "] ---------");
        this.logger.info("On Host [" + this.config.getString("htmlunit.baseUrl") + "]");
        try {
            super.runTest();
        } catch (Throwable t) {
            this.logger.error("Exception in testcase [" + this.testName + "]", t);
            if (this.currentPage != null) {
                WebResponse response = this.currentPage.getWebResponse();
                this.logger.error("Response URL: " + response.getUrl());
                this.logger.error("Response status code: " + response.getStatusCode());
                this.logger.error("Response status message: " + response.getStatusMessage());
                this.logger.error("Response content type: " + response.getContentType());
                this.logger.error("Response content charset: " + response.getContentCharSet());
                this.logger.error("Response load time in ms: "
                        + response.getLoadTimeInMilliSeconds());
                this.logger.info(this.currentPage.asXml());
            }
            // print alerts:
            if (this.collectedAlerts.size() > 0) {
                this.logger.error("Alerts:");
                for (int i = 0; i < this.collectedAlerts.size(); i++) {
                    this.logger.error(this.collectedAlerts.get(i).toString());
                }
            }
            throw t;
        }
        this.logger.info("-------- End testcase [" + this.testName + "] ---------");
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        this.response = null;
        this.currentPage = null;
        super.tearDown();
    }

    /**
     * Sends an HTTP request and handles the response as HTML document.
     * 
     * @param pageURL
     * @throws Exception
     */
    protected void loadHtmlPage(String pageURL) throws Exception {
        URL url = new URL(baseURL + pageURL);
        this.logger.info("Attempting to load page " + url);
        Page page = webClient.getPage(url);
        this.currentPage = handlePage(page);
    }

    /**
     * Click on an element like a button or an anchor (link).
     * 
     * @param element
     * @throws Exception
     */
    protected void click(ClickableElement element) throws Exception {
        Page page = element.click();
        this.currentPage = handlePage(page);
    }

    /**
     * Clicks the given button of the first form in the current page.
     * 
     * @param name
     *            name attribute of the button
     * @throws Exception
     */
    protected void clickButton(String name) throws Exception {
        HtmlForm form = (HtmlForm) this.currentPage.getForms().get(0);
        HtmlSubmitInput button = (HtmlSubmitInput) form.getInputByName(name);
        click(button);
    }

    /**
     * Performs a few tests on a page, then attempts to convert the page to an
     * html page and returns it.
     * 
     * @param page
     * @return
     * @throws Exception
     */
    protected HtmlPage handlePage(Page page) throws Exception {
        this.response = page.getWebResponse();

        assertNotNull("Response contains invalid HTML", page);
        assertTrue("Response should be an HTML page", page instanceof HtmlPage);
        // assertTrue("Response status not 200",
        // this.response.getStatusCode() == 200);

        if (this.logger.isDebugEnabled()) {
            this.logger.debug(((HtmlPage) page).asXml());
        }
        return (HtmlPage) page;
    }

    /**
     * Simulates a click. A link with the provided text must be present on the
     * page
     */
    protected void clickLinkWithText(String linkText) throws Exception {
        HtmlAnchor link = currentPage.getFirstAnchorByText(linkText);
        click(link);
    }

    /**
     * Simulates a click. The path must be present on the page
     */
    protected void clickLink(String linkPath) throws Exception {

        final HtmlElement documentElement = currentPage.getDocumentElement();
        final List links = documentElement.getHtmlElementsByAttribute("a", "href", linkPath);
        HtmlAnchor link = (HtmlAnchor) links.get(0);
        click(link);
    }

    /**
     * Asserts that the title of the current page contains the given string.
     * 
     * @param titleExtract
     */
    protected void assertTitleContains(String titleExtract) {
        String title = currentPage.getTitleText();
        assertFalse("provided text \"" + titleExtract + "\" not found in title \"" + title + "\"",
                title.indexOf(titleExtract) == -1);
    }

    /**
     * Asserts that the title of the current page is equal to the given string.
     * 
     * @param expectedTitle
     */
    protected void assertTitleEquals(String expectedTitle) {
        String title = currentPage.getTitleText();
        assertEquals("Expected page title \"" + expectedTitle + "\" does not match title \"" + title + "\"", expectedTitle, title);
    }

    /**
     * Asserts that the page contains the given string.
     * 
     * @param expectedContent
     */
    protected void assertPageContainsText(String expectedContent) {
        String page = currentPage.asText();
        assertFalse("provided content \"" + expectedContent + "\" not found on this page", page
                .indexOf(expectedContent) == -1);
    }

}
