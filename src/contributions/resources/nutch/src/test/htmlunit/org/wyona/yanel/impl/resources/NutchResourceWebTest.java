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
package org.wyona.yanel.impl.resources;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import java.net.URL;

import org.wyona.yanel.htmlunit.AbstractHtmlUnitTest;

/**
 * Test loading a page which is an xml resource and verifies the title and 
 * page content.
 */
public class NutchResourceWebTest extends AbstractHtmlUnitTest {

    protected void setUp() throws Exception {
        this.testName = "Nutch Resource Web Test";
        super.setUp();
    }

    /**
     * Loads a page and verifies its title.
     */
    public void testNutchResource() throws Exception {
        loadHtmlPage("yanel-website/en/search.html");
        assertTitleEquals("Search - Yanel");
        loadHtmlPage("yanel-website/en/search.html?query=yanel");
        assertTitleEquals("Results for query: yanel - Yanel");
    }
    
/*
    public void testSearchResults() throws Exception {
        String searchTerm = this.config.getString("nutchSearchTerm");
        int totalHits = searchResults(searchTerm);
        int minHints = this.config.getInt("nutchSearchResults");
        assertTrue("search term " + searchTerm + " only found " + totalHits + " times, " +
            "minimum expected was " + minHints + " times", totalHits >= minHints);
    }
*/
    
    /**
     * performs a search and returns the number of hits
     */
    public int searchResults(String searchTerm) throws Exception {
      
        final WebClient webClient = new WebClient();
        final URL url = new URL("http://127.0.0.1:8080/yanel/yanel-website/en/search.html");
        //final URL url = new URL(this.config.getString("baseUrl") + "yanel-website/en/search.html");
        final HtmlPage searchPage = (HtmlPage) webClient.getPage(url);
        final HtmlForm form = searchPage.getFormByName("search");
        final HtmlSubmitInput button = (HtmlSubmitInput) form.getInputByName("submit");
        final HtmlTextInput textField = (HtmlTextInput) form.getInputByName("query");
        textField.setValueAttribute(searchTerm);
        HtmlPage resultPage = (HtmlPage) button.click();
        
        String totalHits = resultPage.getHtmlElementById("resultHits").getAttributeValue("hits");
        return Integer.parseInt(totalHits);
    }
}
