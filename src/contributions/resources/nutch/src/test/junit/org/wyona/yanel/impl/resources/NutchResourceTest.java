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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.io.File;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ModifiableV2;
import org.wyona.yanel.core.map.Map;
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
        Map map = yanel.getMap();
        Realm realm = yanel.getMap().getRealm(url);
        Path path = yanel.getMap().getPath(realm, url);
        this.resource = (NutchResource)yanel.getResourceManager().getResource(null, null, realm, path);
        confDir = this.resource.getRTD().getConfigFile().getParentFile().getAbsolutePath() + 
                    File.separator + "conf" + File.separator;
    }
    
    public void testNutchLocalExists() throws Exception {
        File nutchLocalFile = new File(confDir + "nutch-local.xml");
        //System.err.println("nutch-local --> " + nutchLocalFile.getAbsolutePath());
        assertTrue("the nutch-local dir exists: ", nutchLocalFile.isFile());
    }
    
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
}
