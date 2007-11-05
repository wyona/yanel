/*
 * Copyright 2007 Wyona
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
package org.wyona.yanel.core.util;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.log4j.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Configuration utility to copy an avalon configuration into a DOM document
 */
public class ConfigurationUtil {

    /**
     * The log category instance
     */
    private static final Category log = Category.getInstance(ConfigurationUtil.class);

    /**
     * Create a DOM Document from a custom config element modelled with avalon
     *
     * @param confElement
     *            The configuration element
     * @param rootName
     *            The root element name
     * @param rootNamespace
     *            The target namespace
     * @return
     */
    public static Document getCustomConfiguration(Configuration repoConfigElement, String rootName, String rootNamespace) {
        try {
            if (repoConfigElement == null || repoConfigElement.getChildren() == null || repoConfigElement.getChildren().length == 0) {
                if (repoConfigElement.getValue(null) == null) {
                    log.warn("Did not find any child elements nor text within " + repoConfigElement);
                    return null;
                }
            }

            if (log.isDebugEnabled()) log.debug("Creating custom config - rootName=" + rootName + ", rootNamespace=" + rootNamespace);

            Document doc = null;
            DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            javax.xml.parsers.DocumentBuilder parser = dbf.newDocumentBuilder();
            org.w3c.dom.DOMImplementation impl = parser.getDOMImplementation();
            org.w3c.dom.DocumentType doctype = null;
            doc = impl.createDocument(rootNamespace, rootName, doctype);

            // Copy the very first text node in order to stay backwards compatible
            Element rootElement = doc.getDocumentElement();
            if (repoConfigElement.getValue(null) != null) {
                if (log.isDebugEnabled()) log.debug("Very first text node: " + repoConfigElement.getValue());
                rootElement.appendChild(doc.createTextNode(repoConfigElement.getValue()));
            }

            // Copy elements
            Configuration[] children = repoConfigElement.getChildren();
            if (children.length > 0) {
                log.debug("root element " + rootElement);
                for (int i = 0; i < children.length; i++) {
                    rootElement.appendChild(ConfigurationUtil.createElement(children[i], doc));
                }
            }
            return doc;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

        // original comment by michi:
        // avalon ConfigurationUtil doesn't seem to work properly
        /*
         * org.w3c.dom.Element element = ConfigurationUtil.toElement(customConfig);
         * log.error("DEBUG: element: " + element.getLocalName()); org.w3c.dom.Document doc =
         * element.getOwnerDocument(); org.w3c.dom.Element rootElement = doc.getDocumentElement();
         * rootElement.appendChild(element); return doc;
         */
    }

    /**
     * Parse a configuration node into a element and add it to the document. <br/>This method may
     * call itself recusively.
     *
     * @param conf
     *            The configuration element to parse
     * @param doc
     *            The DOM document to create elements for
     *
     * @return Returns the created element
     */
    private static Element createElement(Configuration config, Document doc) throws Exception {

        Element element = doc.createElementNS(config.getNamespace(), config.getName());
        String[] attrs = config.getAttributeNames();
        for (int i = 0; i < attrs.length; i++) {
            element.setAttributeNS(config.getNamespace(), attrs[i], config.getAttribute(attrs[i]));
        }
        // TODO: Does not work for elements with mixed content (text and
        // elements)
        try {
            element.appendChild(doc.createTextNode(config.getValue()));
        } catch (Exception e) {
            log.debug("No value: " + element.getLocalName() + "  - skipped child creation");
        }
        Configuration[] children = config.getChildren();
        if (children.length > 0) {
            for (int i = 0; i < children.length; i++) {
                element.appendChild(ConfigurationUtil.createElement(children[i], doc));
            }
        }
        return element;
    }
}
