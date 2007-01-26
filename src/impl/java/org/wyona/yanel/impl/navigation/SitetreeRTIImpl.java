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

package org.wyona.yanel.impl.navigation;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.navigation.Node;
import org.wyona.yanel.core.navigation.Sitetree;

import org.apache.log4j.Category;

/**
 * Based on the RTI of each realm
 */
public class SitetreeRTIImpl implements Sitetree {

    private static Category log = Category.getInstance(SitetreeRTIImpl.class);

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
        org.wyona.yarep.core.Repository repo = null;
        try {
            repo = realm.getRTIRepository();
        } catch(Exception e) {
            log.error(e);
        }
        return new NodeRTIImpl(repo, path);
    }

    /**
     *
     */
    public Node createNode(String name) {
        return null;
    }
}
