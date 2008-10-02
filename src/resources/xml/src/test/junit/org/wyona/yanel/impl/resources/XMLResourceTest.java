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

import org.wyona.yanel.core.api.attributes.ModifiableV2;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.junit.AbstractYanelTest;

/**
 * Test for the XML Resource.
 */
public class XMLResourceTest extends AbstractYanelTest {

    protected XMLResource resource;

    public void setUp() throws Exception {
        super.setUp();
        this.testName = "Test for the XML Resource";
        
        Realm realm = yanel.getMap().getRealm("/test/");
        String path = "/test/test.html";
        resource = new XMLResource();
        resource.setPath(path);
        resource.setRealm(realm);
        
        /*
        String url = "/test/test-xml-resource.html";
        Map map = yanel.getMap();
        Realm realm = yanel.getMap().getRealm(url);
        Path path = yanel.getMap().getPath(realm, url);
        this.resource = yanel.getResourceManager().getResource(null, null, realm, path);
        */
    }

    public void testWriteRead() throws Exception {

        // Write resource
        OutputStream os = ((ModifiableV2)this.resource).getOutputStream();
        assertNotNull("getOutputStream() should not return null for path: " + this.resource.getPath(), os);
        Writer writer = new OutputStreamWriter(os, "UTF-8");
        String testContent = "Hello World! " + System.currentTimeMillis();

        writer.write(testContent);
        writer.close();

        // Read resource
        InputStream is = ((ModifiableV2)this.resource).getInputStream();
        assertNotNull("getInputStream() should not return null for path: " + this.resource.getPath(), is);
        Reader reader = new InputStreamReader(is, "UTF-8");
        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        br.close();
        reader.close();

        assertEquals(line, testContent);

    }

}
