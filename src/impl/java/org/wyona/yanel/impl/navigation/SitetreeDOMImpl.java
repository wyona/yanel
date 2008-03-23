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

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.map.RealmConfigPathResolver;
import org.wyona.yanel.core.navigation.Node;
import org.wyona.yanel.core.navigation.Sitetree;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Based on DOM, whereas persistance is done through the src configuration, e.g.
 * <repo-navigation class="org.wyona.yanel.impl.navigation.SitetreeDOMImpl">
 *   <src>yanelrepo:/sitetree.xml</src>
 * </repo-navigation>
 */
public class SitetreeDOMImpl implements Sitetree {

    private static Logger log = Logger.getLogger(SitetreeDOMImpl.class);

    // IMPORTANT: Consider memory and redundancy issues!
    private Document sitetreeDoc;
    private String src;

    /**
     * @see
     */
    public void init(Document configDoc, RealmConfigPathResolver resolver) {
        NodeList nl = configDoc.getDocumentElement().getElementsByTagName("src");
        if (nl.getLength() == 1) {
            src = nl.item(0).getFirstChild().getNodeValue();
            if(log.isDebugEnabled()) log.debug("src: " + src + ", " + nl.item(0).getNodeName());
            log.error("DEBUG: src: " + src + ", " + nl.item(0).getNodeName());
            try {
                javax.xml.transform.Source source = resolver.resolve(src, null);
                javax.xml.parsers.DocumentBuilderFactory dbf= javax.xml.parsers.DocumentBuilderFactory.newInstance();
                javax.xml.parsers.DocumentBuilder parser = dbf.newDocumentBuilder();
                sitetreeDoc = parser.parse(new java.io.FileInputStream(source.getSystemId()));
                //sitetreeDoc = parser.parse(((javax.xml.transform.stream.StreamSource) source).getInputStream());
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
        return new NodeDOMImpl(sitetreeDoc.getDocumentElement());
    }

    /**
     *
     */
    public Node getNode(Realm realm, String path) {
        //log.debug("Path: " + path);
        try {
            if (path.equals("/")) {
                return new NodeDOMImpl(sitetreeDoc.getDocumentElement());
            } else if (path.startsWith("/") && path.length() > 1) {
                Element element = getElement(sitetreeDoc.getDocumentElement(), path);
                if (element != null) {
                    return new NodeDOMImpl(element);
                } else {
                    log.error("No node for path: " + path);
                    return null;
                }
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
     *
     */
    public Node createNode(String name) {
        log.error("TODO: Implementation not finished yet!");
        return null;
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
               //log.debug("Subtree path: " + subtreePath);
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
                   //log.debug("Subtree path: " + subtreePath);
                   return getElement(parent, subtreePath);
               }
               return child;
           } else {
               log.error("No such node: " + path);
               return null;
           }
       } else {
           return sitetreeDoc.getDocumentElement();
       }
    }
}
