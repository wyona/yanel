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

package org.wyona.yanel.impl.map;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.map.Map;

import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;

import org.apache.log4j.Category;

/**
 *
 */
public class MapImpl implements Map {

    private static Category log = Category.getInstance(MapImpl.class);

    Repository repo;

    /**
     *
     */
    public MapImpl() {
        try {
            repo = new RepositoryFactory().newRepository("yanel");
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     *
     */
    public String getUUID() {
        return "sugus";
    }

    /**
     * See James Clark's explanation on namespaces: http://www.jclark.com/xml/xmlns.htm
     */
    public String getResourceTypeIdentifier(Path path) {
        try {
            // TODO: See YarepSource re using more than one repository!
            java.io.BufferedReader br = new java.io.BufferedReader(repo.getReader(new org.wyona.yarep.core.Path(path.toString())));
            return br.readLine();
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
        log.warn("No resource type identifier for path: " + path);
        return null;
    }
}
