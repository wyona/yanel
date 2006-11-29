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
package org.wyona.yanel.junit;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.wyona.yanel.core.Yanel;

import junit.framework.TestCase;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SimpleLog;

/**
 * Base class for yanel junit tests.
 */
public abstract class AbstractYanelTest extends TestCase {

    /** configuration */
    protected Configuration config;

    /**
     * Logger for informative output by test cases. The output level can be
     * specified in properties.xml
     */
    protected Log logger;

    /**
     * The name of the testcase, used for debug output.
     */
    protected String testName;

    protected File baseDir;

    protected Yanel yanel;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        // read the config file
        URL configURL = this.getClass().getClassLoader().getResource("junit-properties.xml");

        config = new XMLConfiguration(configURL);

        SimpleLog log = new SimpleLog("junit log");
        log.setLevel(Integer.parseInt(this.config.getString("junit.debugLevel")));
        this.logger = log;

        this.yanel = Yanel.getInstance();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#runTest()
     */
    protected void runTest() throws Throwable {
        this.logger.info("-------- Begin testcase [" + this.testName + "] ---------");
        try {
            super.runTest();
        } catch (Throwable t) {
            this.logger.error("Exception in testcase [" + this.testName + "]", t);
            throw t;
        } finally {
            this.logger.info("-------- End testcase [" + this.testName + "] ---------");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
