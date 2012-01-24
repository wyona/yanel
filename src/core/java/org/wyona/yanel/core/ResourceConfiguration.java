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

package org.wyona.yanel.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.log4j.Logger;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wyona.commons.xml.XMLHelper;
import org.wyona.yanel.core.util.ConfigurationUtil;
import org.wyona.yarep.core.Node;

/**
 * Resource configuration.
 * Based on a yarep node.
 */
public class ResourceConfiguration {

    private Logger log = Logger.getLogger(ResourceConfiguration.class);

    protected Node node;
    protected String name;
    protected String namespace;
    protected String encoding = null;
    protected LinkedHashMap properties;
    protected Document customConfig;
    
    private static final String NAMESPACE_1_0 = "http://www.wyona.org/yanel/rti/1.0";
    private static final String NAMESPACE_1_1 = "http://www.wyona.org/yanel/rti/1.1"; // INFO: According to http://semver.org we have increased the minor version because we have introduced the backwards compatible functionality of a 'target environment'
    
    /**
     * Creates a resource configuration from a yarep node.
     */
    public ResourceConfiguration(Node node) throws Exception {
        this.node = node;
        load(node.getInputStream());
    }
    
    /**
     * Creates a resource configuration from an InputStream.
     * NOTE: If you want to be able to save the resource, use ResourceConfiguration(Node node) instead. If a rc is created using this constructor, saving won't work.
     */
    public ResourceConfiguration(InputStream in) throws Exception {
        load(in);
    }
    
    /**
     * Create a resource configuration from a property map.
     * NOTE: If you want to be able to save the resource, use ResourceConfiguration(Node node) instead. If a rc is created using this constructor, saving won't work.
     * @param name Resource Type Name
     * @param namespace Resource Type Namespace
     */
    public ResourceConfiguration(String name, String namespace, Map properties) {
        this.name = name;
        this.namespace = namespace;

        if (properties != null) {
            this.properties = new LinkedHashMap(properties);
        } else {
            this.properties = new LinkedHashMap();
            
/*            String LOCATION = "resource_config_location";
            String PREFIX = "yanel";
            String RC_NAMESPACE = "http://www.wyona.org/yanel/rti/1.0";
            config = new DefaultConfiguration("resource-config", LOCATION, RC_NAMESPACE, PREFIX);
            DefaultConfiguration rti = new DefaultConfiguration("rti", LOCATION, RC_NAMESPACE, PREFIX);
            rti.setAttribute("name", name);
            rti.setAttribute("namespace", namespace);
            config.addChild(rti);

            java.util.Iterator keyIterator = properties.keySet().iterator();
            while (keyIterator.hasNext()) {
                DefaultConfiguration property = new DefaultConfiguration("property", LOCATION, RC_NAMESPACE, PREFIX);
                String key = (String) keyIterator.next();
                property.setAttribute("name", key);
                property.setAttribute("value", (String) properties.get(key));
                config.addChild(property);
            }
*/        }
    }
    
    /**
     * Read resource configuration.
     * @param in Input stream containing resource configuration as XML
     * @throws Exception
     */
    public void load(InputStream in) throws Exception {
        this.properties = new LinkedHashMap();
        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder(true);
        DefaultConfiguration config = (DefaultConfiguration) builder.build(in);
        String rcNamespace = config.getNamespace();

        if (!rcNamespace.equals(NAMESPACE_1_0) && !rcNamespace.equals(NAMESPACE_1_1)) {
            log.error("Namespace is not supported: " + rcNamespace);
            throw new Exception("Namespace is not supported: " + rcNamespace);
        }

        Configuration rtiConfig = config.getChild("rti");
        this.name = rtiConfig.getAttribute("name");
        this.namespace = rtiConfig.getAttribute("namespace");
        log.debug("Universal Name: " + getUniversalName());

        // encoding seems to be a special case (?)
        Configuration encodingConfig = config.getChild("encoding", false);
        if (encodingConfig != null) encoding = encodingConfig.getValue();

        LinkedHashMap listProperties = new LinkedHashMap();
        
        // INFO: Read properties into a map of arraylists:
        Configuration[] props = config.getChildren("property");
        for (int i = 0; i < props.length; i++) {
            String name = props[i].getAttribute("name");
            String value = props[i].getAttribute("value");
            String targetEnv = props[i].getAttribute("target-environment", null);

            if (rcNamespace.equals(NAMESPACE_1_1) && targetEnv!= null) {
                log.warn("Property '" + name + "' has set target environment: " + targetEnv);
            }

            if (listProperties.containsKey(name)) {
                ArrayList arrayList = (ArrayList)listProperties.get(name);
                arrayList.add(value);
            } else {
                ArrayList arrayList = new ArrayList();
                arrayList.add(value);
                listProperties.put(name, arrayList);
            }
        }
        
        // now convert the arraylists with a single entry to normal properties:
        Iterator iter = listProperties.keySet().iterator();
        while (iter.hasNext()) {
            String name = (String)iter.next();
            ArrayList arrayList = (ArrayList)listProperties.get(name);
            if (arrayList.size() == 1) {
                this.properties.put(name, arrayList.get(0));
            } else {
                String[] values = (String[])arrayList.toArray(new String[arrayList.size()]);
                this.properties.put(name, values);
            }
        }

        // load custom config:
        Configuration customConfig = config.getChild("custom-config", false);
        if (customConfig != null) {
            this.customConfig = ConfigurationUtil.getCustomConfiguration(customConfig, customConfig.getName(), 
                customConfig.getNamespace());
        }
        
    }
    
    /**
     * Get universal name of resource type
     */
    public String getUniversalName() {
        return "<{" + namespace + "}" + name + "/>";
    }
    
    /**
     * Get resource type name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Get resource type namespace
     */
    public String getNamespace() {
        return this.namespace;
    }
    
    /**
     * Get encoding respectively charset
     */
    public String getEncoding() {
        return this.encoding;
    }
    
    /**
     * Get the value of a particular property. If the property is multi-valued, the first value will be returned.
     * @param key Name of property
     * @return value of property with a particular key/name or null if no value exists for this key/name.
     */
    public String getProperty(String key) throws Exception {
        log.warn("DEBUG: Get property value of key '" + key + "'...");
        Object obj = this.properties.get(key);
        if (obj instanceof String) {
            return (String)obj; 
        } else if (obj instanceof String[]) {
            return ((String[])obj)[0];
        } else {
            return null;
        }
    }
    
    /**
     * Sets a property.
     * NOTE: does not save the configuration.
     * @param key
     * @param value
     * @throws Exception
     */
    public void setProperty(String key, String value) throws Exception {
        this.properties.put(key, value);
    }
    
    /**
     * Get the values of a multi-valued property.
     * If the property is not multi-valued, then it will return an array of length one.
     * @param key Name of property
     * @return value(s) for this key or an empty array if no value(s) exists for this key.
     */
    public String[] getProperties(String key) throws Exception {
        Object obj = this.properties.get(key);
        if (obj instanceof String[]) {
            return (String[])obj;
        } else if (obj instanceof String) {
            String[] array = new String[1];
            array[0] = (String)obj;
            return array;
        } else {
            return new String[0];
        }
    }
    
    /**
     * Sets a multivalued property.
     * NOTE: does not save the configuration.
     * @param key
     * @param values
     * @throws Exception
     */
    public void setProperties(String key, String[] values) throws Exception {
        this.properties.put(key, values);
    }
    
    /**
     * Check if property exists
     */
    public boolean containsKey(String key) throws Exception {
        return properties.containsKey(key);
    }

    /**
     * Get yanel:custom-config. Returns null if resource config does not contain a custom-config element.
     */
    public Document getCustomConfiguration() {
        return this.customConfig;
    }
    
    /**
     * Sets the custom config.
     * NOTE: does not save the configuration.
     * @param document dom document
     */
    public void setCustomConfiguration(Document document) {
        this.customConfig = document;
    }
    
    /**
     * Saves the resource configuration to the yarep node.
     */
    public void save() throws Exception {
        if (this.node == null) {
            throw new Exception("cannot save resource configuration because the node is null.");
        }
        Document document;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder parser = dbf.newDocumentBuilder();
        DOMImplementation impl = parser.getDOMImplementation();
        DocumentType doctype = null;
        document = impl.createDocument(NAMESPACE_1_0, "yanel:resource-config", doctype);

        Element rootElement = document.getDocumentElement();
        rootElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:yanel", NAMESPACE_1_0);
        
        Element rtiElement = document.createElementNS(NAMESPACE_1_0, "yanel:rti");
        rtiElement.setAttribute("name", this.name);
        rtiElement.setAttribute("namespace", this.namespace);
        rootElement.appendChild(rtiElement);
        
        // add encoding:
        if (this.encoding != null) {
            Element encodingElement = document.createElementNS(NAMESPACE_1_0, "yanel:encoding");
            encodingElement.setNodeValue(this.encoding);
            rootElement.appendChild(encodingElement);
        }

        // add properties:
        Iterator keyIterator = properties.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = (String) keyIterator.next();
            Object obj = properties.get(key);
            if (obj instanceof String) {
                Element property = document.createElementNS(NAMESPACE_1_0, "yanel:property");
                property.setAttribute("name", key);
                property.setAttribute("value", (String) obj);
                rootElement.appendChild(property);
            } else if (obj instanceof String[]) {
                String[] values = (String[])obj;
                for (int i = 0; i < values.length; i++) {
                    Element property = document.createElementNS(NAMESPACE_1_0, "yanel:property");
                    property.setAttribute("name", key);
                    property.setAttribute("value", values[i]);
                    rootElement.appendChild(property);
                }
            } 
        }
        
        // add custom config:
        if (this.customConfig != null) {
            org.w3c.dom.Node customNode = document.importNode(this.customConfig.getDocumentElement(), true);
            Element customElement = document.createElementNS(NAMESPACE_1_0, "yanel:custom-config");
            NodeList children = customNode.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                customElement.appendChild(children.item(i));
            }
            rootElement.appendChild(customElement);
        }
        
        XMLHelper.writeDocument(document, this.node.getOutputStream());
    }
    
    /**
     * Get yarep node if one exists
     * @return Yarep node
     */
    public Node getNode() {
        if (this.node == null) {
            log.warn("This resource configuration '" + getUniversalName() + "' does not seem to be based on a yarep node!");
            return null;
        }
        return node;
    }
}
