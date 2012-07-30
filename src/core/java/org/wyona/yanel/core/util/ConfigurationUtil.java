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
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.DefaultConfigurationSerializer;
import org.apache.avalon.framework.configuration.MutableConfiguration;
import org.apache.commons.io.output.ByteArrayOutputStream;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;

/**
 * Configuration utility to deal with Avalon configuration elements.
 */
public class ConfigurationUtil {

    /**
     * The log category instance
     */
    private static final Logger log = Logger.getLogger(ConfigurationUtil.class);
    
    /**
     * Filter elements by target environment.
     * @throws IOException 
     * @throws SAXException 
     */
    public static InputStream filterEnvironment(InputStream istream, String targetEnvironment) throws ConfigurationException, SAXException, IOException {
    	DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
    	Configuration sched_config = builder.build(istream);
    	MutableConfiguration mutable_config = new DefaultConfiguration(sched_config);
    	Configuration sched_config_filtered = ConfigurationUtil.filterEnvironment(mutable_config, targetEnvironment);
    	DefaultConfigurationSerializer serializer = new DefaultConfigurationSerializer(); 
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	serializer.serialize(baos, sched_config_filtered);
    	return new ByteArrayInputStream(baos.toByteArray());
    }
    
    /**
     * Filter elements by target environment
     * @param configElement The config element.
     * @param targetEnvironment The target environment.
     */
    public static Configuration filterEnvironment(MutableConfiguration configElement, String targetEnvironment) throws ConfigurationException {
        if(targetEnvironment == null) {
            log.debug("No target environment configured, hence remove all elements which have a target environment attribute...");
            targetEnvironment = ""; // INFO: Assuming that there are no empty target environment attributes (!), we set it to empty, such that the attribute values won't match, and hence those elements will be removed...
        }

        Configuration[] children = configElement.getChildren();
        // INFO: Generate map of children with same name
        Map<String, Queue<Configuration>> elementsMap = new HashMap<String, Queue<Configuration>>();
        for (int i = 0; i < children.length; i++) {
            String name = children[i].getName();
            Queue<Configuration> elements;
            if(elementsMap.containsKey(name)) {
                elements = elementsMap.get(name);
            } else {
                elements = new LinkedList<Configuration>();
                elementsMap.put(name, elements);
            }
            elements.add(children[i]);
        }

        for(Map.Entry<String, Queue<Configuration>> entry : elementsMap.entrySet()) {
            log.debug("Look at every element with the same name '" + entry.getKey() + "' we found...");
            Queue<Configuration> elements = entry.getValue();
            boolean targetEnvMatched = false;

            // INFO: Check first whether there are elements with the same that have an attribute named target-environment, because otherwise we might remove too much
            for(Configuration child : elements) {
                try {
                    String env = child.getAttribute("target-environment");
                    log.debug("Element '" + child.getName()+ "' has target environment attribute set: " + child.getAttribute("target-environment"));
                    if(targetEnvironment.equals(env)) {
                        log.debug("The target environment of the element '" + child.getName() + "' did match: " + env);
                        if (targetEnvMatched) {
                            log.warn("There are two elements with the same name '" + child.getName() + "' which matched the target environment: " + env);
                        }
                        targetEnvMatched = true;
                    } else {
                        configElement.removeChild(child);
                    }
                } catch(ConfigurationException e) {
                    // No target-environment attribute: Exception is thrown if attribute is not set.
                    log.debug("Element '" + child.getName()+ "' has no target environment attribute set.");
                }
            }
            	
            if(targetEnvMatched) {
                for(Configuration child : elements) {
                    try {
                        String env = child.getAttribute("target-environment");
                        if(!targetEnvironment.equals(env)) {
                            log.warn("There should be no more element '" + child.getName() + "' where target environment attribute does not match: " + child.getAttribute("target-environment"));
                        }
                    } catch(ConfigurationException e) {
                        log.debug("Remove element '" + child.getName() + "' without target environment attribute.");
                        configElement.removeChild(child);
                    }
                }
            }
        }

        // INFO: Filter elements recursively
        for (int i = 0; i < children.length; i++) {
            children[i] = filterEnvironment((MutableConfiguration) children[i], targetEnvironment);
        }

        return configElement;
    }
    
    /**
     * Create a DOM Document from a custom config element modelled with avalon
     *
     * @param repoConfigElement
     *            The configuration element
     * @param rootName
     *            The root element name
     * @param rootNamespace
     *            The target namespace
     * @return Custom config as DOM
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
          org.w3c.dom.Element element = ConfigurationUtil.toElement(customConfig);
          log.debug("Element name: " + element.getLocalName());
          org.w3c.dom.Document doc = element.getOwnerDocument();
          org.w3c.dom.Element rootElement = doc.getDocumentElement();
          rootElement.appendChild(element); return doc;
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
