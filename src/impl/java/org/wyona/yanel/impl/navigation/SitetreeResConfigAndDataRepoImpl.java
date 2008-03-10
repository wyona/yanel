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
import org.wyona.yanel.core.map.RealmConfigPathResolver;
import org.wyona.yanel.core.navigation.Node;
import org.wyona.yanel.core.navigation.Sitetree;

import org.apache.log4j.Category;

import org.w3c.dom.Document;

/**
 * Based on the resource configuration repository and data repository of each realm
 */
public class SitetreeResConfigAndDataRepoImpl implements Sitetree {

    private static Category log = Category.getInstance(SitetreeResConfigAndDataRepoImpl.class);

    /**
     *
     */
    public void init(Document document, RealmConfigPathResolver resolver) {
        // Not needed yet.
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
        org.wyona.yarep.core.Repository resConfigRepo = null;
        org.wyona.yarep.core.Repository dataRepo = null;
        try {
            resConfigRepo = realm.getRTIRepository();
            dataRepo = realm.getRepository();
        } catch(Exception e) {
            log.error(e);
        }
        return new NodeResConfigAndDataRepoImpl(resConfigRepo, dataRepo, path);
    }

    /**
     *
     */
    public Node createNode(String name) {
        return null;
    }
}
