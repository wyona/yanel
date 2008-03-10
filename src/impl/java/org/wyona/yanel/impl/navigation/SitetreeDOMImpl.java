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
        } else {
            log.error("Number of elements with tag name \"src\" is not equal one!");
        }
    }

    /**
     *
     */
    public Node getSitetreeNode() {
        return null;
    }

    /**
     *
     */
    public Node getNode(Realm realm, String path) {
        return null;
/*
        org.wyona.yarep.core.Repository resConfigRepo = null;
        org.wyona.yarep.core.Repository dataRepo = null;
        try {
            resConfigRepo = realm.getRTIRepository();
            dataRepo = realm.getRepository();
        } catch(Exception e) {
            log.error(e);
        }
        return new NodeResConfigAndDataRepoImpl(resConfigRepo, dataRepo, path);
*/
    }

    /**
     *
     */
    public Node createNode(String name) {
        return null;
    }
}
