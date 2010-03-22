/*
 * Copyright 2008 Wyona
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

package org.wyona.yanel.impl.navigation;

import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.navigation.Node;
import org.wyona.yanel.core.navigation.Sitetree;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Based on DOM, whereas persistance is done through the <code>src</code> configuration, for example within <code>realm.xml</code>
 * <pre>
 * &lt;repo-navigation class="org.wyona.yanel.impl.navigation.SitetreeDOMImpl">
 *   &lt;src>data-repo/data/sitetree.xml&lt;/src>
 * &lt;/repo-navigation>
 * </pre>
 *
 * or <code>.yanel-rc</code>
 *
 * <pre>
 * &lt;yanel:custom-config>
 *   &lt;s:repo-navigation xmlns:s="http://www.wyona.org/yanel/sitetree-dom-impl/1.0" class="org.wyona.yanel.impl.navigation.SitetreeDOMImpl">
 *     &lt;s:src>yanelrepo:/sitetree.xml&lt;/s:src>
 *   &lt;/s:repo-navigation>
 * &lt;/yanel:custom-config>
 * </pre>
 *
 * Please note that the class <code>org.wyona.yanel.core.map.Realm</code> 
 * is using the <code>RealmConfigPathResolver</code> whereas that a resource might use a different resolver implementation!
 */
public class SitetreeDOMImpl implements Sitetree {

    private static Logger log = Logger.getLogger(SitetreeDOMImpl.class);

    private static String SITETREE_NAMESPACE = "http://www.wyona.org/yanel/sitetree/1.0";

    // IMPORTANT: Consider memory and redundancy issues!
    private Document sitetreeDoc;
    private String src;

    private String systemId;

    /**
     * @see
     */
    public void init(Document configDoc, javax.xml.transform.URIResolver resolver) {
        NodeList nl = configDoc.getDocumentElement().getElementsByTagName("src");
        if (nl.getLength() == 1) {
            src = nl.item(0).getFirstChild().getNodeValue();
            if(log.isDebugEnabled()) {
                log.debug("src: " + src + ", " + nl.item(0).getNodeName());
            }
            try {
                javax.xml.transform.Source source = resolver.resolve(src, null);
                javax.xml.parsers.DocumentBuilderFactory dbf= javax.xml.parsers.DocumentBuilderFactory.newInstance();
                javax.xml.parsers.DocumentBuilder parser = dbf.newDocumentBuilder();
                systemId = source.getSystemId();
                //sitetreeDoc = parser.parse(new java.io.FileInputStream(source.getSystemId()));
                sitetreeDoc = parser.parse(((javax.xml.transform.stream.StreamSource) source).getInputStream());
            } catch (Exception e) {
                log.error(e, e);
            }
        } else {
            log.error("Number of elements with tag name \"src\" is not equal one!");
        }
    }

    /**
     *
     */
    public Node getSitetreeNode() {
        return new NodeDOMImpl(sitetreeDoc.getDocumentElement(), this);
    }

    /**
     *
     */
    public Node getNode(Realm realm, String path) {
        //log.debug("Path: " + path);
        try {
            if (path.equals("/")) {
                if (sitetreeDoc != null) {
                    return new NodeDOMImpl(sitetreeDoc.getDocumentElement(), this);
                }
               log.error("It seems like the sitetree '" + src + "' was not initialized properly!");
               return null;
            } else if (path.startsWith("/") && path.length() > 1) {
                Element element = getElement(sitetreeDoc.getDocumentElement(), path);
                if (element != null) {
                    return new NodeDOMImpl(element, this);
                }
                log.error("No node for path: " + path);
                return null;
            } else {
                log.error("Path is not valid: " + path);
                return null;
            }
        } catch(Exception e) {
            log.error(e);
            return null;
        }
    }

    /**
     * @see
     */
    public Node createNode(String name, String label) {
        Element newElement = sitetreeDoc.createElementNS(SITETREE_NAMESPACE, "node");
        newElement.setAttributeNS(SITETREE_NAMESPACE, "name", name);
        Element labelElement = sitetreeDoc.createElementNS(SITETREE_NAMESPACE, "label");
        labelElement.appendChild(sitetreeDoc.createTextNode(label));
        newElement.appendChild(labelElement);
        return new NodeDOMImpl(newElement, this);
    }

    /**
     * @param parent Parent element
     * @param path Subtree path
     */
    private org.w3c.dom.Element getElement(org.w3c.dom.Element parent, String path) throws Exception {
       String[] names = path.split("/");
       //log.debug("Path: " + path);
       //log.debug("Length: " + names.length);

       String childName = null;
       String subtreePath = null;
       if (names.length > 1) {
           childName = names[1];
           //log.debug("Child: " + childName);
           if (names.length > 2) {
               subtreePath = "/" + names[2];
               for (int i = 3; i < names.length; i++) {
                   subtreePath = subtreePath + "/" + names[i];
               }
               log.debug("Subtree path: " + subtreePath);
           } else {
               //log.debug("No subtree.");
           }
       } else {
           //log.debug("The end: " + path);
       }

       if (childName != null) {
           //log.debug("Child: " + childName);
           NodeList nl = parent.getChildNodes();
           Element child = null;
           for (int i = 0; i < nl.getLength(); i++) {
               if (nl.item(i).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE && nl.item(i).getNodeName().equals("node") && ((Element) nl.item(i)).getAttribute("name").equals(childName)) {
                   child = (Element) nl.item(i);
                   break;
               }
           }
           if (child != null) {
               if (subtreePath != null) {
                   log.debug("Subtree path: " + subtreePath);
                   return getElement(child, subtreePath);
               }
               return child;
           }
        log.error("No such node: " + path);
           return null;
       }
    return sitetreeDoc.getDocumentElement();
    }

    /**
     * Save sitetree to file system (based on resolved source system id)
     */
    public void save() {
        try {
            org.wyona.commons.xml.XMLHelper.writeDocument(sitetreeDoc, new java.io.FileOutputStream(systemId));
            log.warn("Sitetree has been written into file system: " + systemId);
            //log.warn("Sitetree has been written into persistent repository: " + systemId);
        } catch(Exception e) {
            log.error(e, e);
        }
    }
}
