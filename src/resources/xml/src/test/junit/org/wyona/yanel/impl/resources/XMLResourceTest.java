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

import org.wyona.yanel.core.Path;
import org.wyona.yanel.junit.AbstractYanelTest;

/**
 * Test for the XML Resource.
 */
public class XMLResourceTest extends AbstractYanelTest {

    protected XMLResource resource;

    protected Path path;

    public void setUp() throws Exception {
        super.setUp();
        this.testName = "Test for the XML Resource";
        this.resource = new XMLResource();
        this.path = new Path("/yanel-website/test-xml-resource.html");
        this.resource.setYanel(this.yanel);

    }

    public void testWriteRead() throws Exception {

        // Write resource
        OutputStream os = this.resource.getOutputStream(path);
        assertNotNull("getOutputStream() should not return null for path: " + path, os);
        Writer writer = new OutputStreamWriter(os, "UTF-8");
        String testContent = "Hello World! " + System.currentTimeMillis();

        writer.write(testContent);
        writer.close();

        // Read resource
        InputStream is = this.resource.getInputStream(path);
        assertNotNull("getInputStream() should not return null for path: " + path, is);
        Reader reader = new InputStreamReader(is, "UTF-8");
        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        br.close();
        reader.close();

        assertEquals(line, testContent);

    }

}
