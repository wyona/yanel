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

import java.io.File;

import org.wyona.yanel.core.Environment;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.junit.AbstractYanelTest;

/**
 * Test for the XML Resource.
 */
public class NutchResourceTest extends AbstractYanelTest {

    protected NutchResource resource;
    protected String confDir = null;

    public void setUp() throws Exception {
        super.setUp();
        this.testName = "Test for the NUTCH Resource";
        
        String url = "/yanel-website/en/search.html";
        Realm realm = yanel.getMap().getRealm(url);
        String path = yanel.getMap().getPath(realm, url);
        Environment environment = new Environment(null, null, null, null, null);
        this.resource = (NutchResource)yanel.getResourceManager().getResource(environment, realm, path);
        confDir = this.resource.getRTD().getConfigFile().getParentFile().getAbsolutePath() + 
                    File.separator + "conf" + File.separator;
    }

    public void testDummy() throws Exception {
    }
    
/*
    public void testNutchLocalExists() throws Exception {
        File nutchLocalFile = new File(confDir + "nutch-local.xml");
        //System.err.println("nutch-local --> " + nutchLocalFile.getAbsolutePath());
        assertTrue("the nutch-local dir exists: ", nutchLocalFile.isFile());
    }
*/
    
/*
    public void testCrawlDirectoryExists() throws Exception {
        org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
        java.net.URL defaultResource = new java.net.URL("file:" + confDir + "nutch-default.xml");
        configuration.addDefaultResource(defaultResource);
        java.net.URL finalResource = new java.net.URL("file:" + confDir + "nutch-local.xml");
        configuration.addFinalResource(finalResource);
        File crawlDir = new File(configuration.get("searcher.dir"));
        //System.err.println("crawlDir --> " + crawlDir.getAbsolutePath());
        assertTrue("crawl dir exists: ", crawlDir.exists());
    }
*/
}
