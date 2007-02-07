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

package org.wyona.yanel.core.map;

import org.wyona.yanel.core.Path;

/**
 * Mapping from urls to realms/paths.
 */
public interface Map {

    /**
     *
     */
    public String getUUID();

    /**
     * @deprecated
     */
    public String getResourceTypeIdentifier(Path path);

    /**
     * @deprecated
     */
    public Realm[] getRealms();

    /**
     * @deprecated
     */
    public Realm getRealm(Path path);

    

    public void setRealmConfiguration(RealmConfiguration realmConfig);

    /**
     * Gets the realm for the given url, according to the configuration in realms.xml.
     * 
     * @param url url without context path prefix
     * @return the realm with the longest mount-point matching the given url.
     * @throws Exception
     */
    public Realm getRealm(String url) throws Exception;
    
    /**
     * Maps the given url to a path in the given realm. A path is relative to the realm.
     * @param realm
     * @param url
     * @throws Exception
     */
    public String getPath(Realm realm, String url) throws Exception;
}
