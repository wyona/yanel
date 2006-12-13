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

import org.apache.tools.ant.taskdefs.optional.junit.XMLResultAggregator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ResultAggregator extends XMLResultAggregator{
    
    /**
     * Aggregate all testsuites into a single document return it as a dom doucment
     * @return an aggregation of all testsuites as dom document
     */
    public Document getDocument() {
        Element rootElement = createDocument();
        Document doc;
        doc = rootElement.getOwnerDocument();
        
        return doc;
    }
}