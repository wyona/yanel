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
import org.wyona.yanel.core.Yanel;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.map.RealmConfiguration;

import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yarep.util.RepoPath;
import org.wyona.yarep.util.YarepUtil;

import org.apache.log4j.Category;

/**
 *
 */
public class MapImpl implements Map {

    private static Category log = Category.getInstance(MapImpl.class);

    RealmConfiguration realmConfig;

    /**
     *
     */
    public MapImpl() {
        try {
            // NOTE: Separate ResourceTypeIdentifier mapping from whatever else is using yarep ...

            realmConfig = new RealmConfiguration();
        } catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    
    protected RepositoryFactory getRepositoryFactory() throws Exception {
        return Yanel.getInstance().getRepositoryFactory("RTIRepositoryFactory");
        //repoFactory = new RepositoryFactory();
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
        log.debug("Original path: " + path);
        try {
            RepoPath rp = new YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()),getRepositoryFactory());
            Repository repo = rp.getRepo();
            log.debug("Repo Name: " + repo.getName());
            log.debug("New path: " + rp.getPath());

            java.io.BufferedReader br = new java.io.BufferedReader(repo.getReader(new org.wyona.yarep.core.Path(new Path(rp.getPath().toString()).getRTIPath().toString())));
            return br.readLine();
        } catch(NoSuchNodeException e) {
            log.warn(e.getMessage());
            log.warn("TODO: Implement chain of responsibility ...");
            return "<{http://www.wyona.org/yanel/resource/1.0}file/>";
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            log.warn("No resource type identifier for path: " + path);
            return null;
        }
    }

    /**
     *
     */
    public Realm[] getRealms() {
        try {
            RepositoryFactory repoFactory = getRepositoryFactory();
            String[] repoIds = repoFactory.getRepositoryIDs();
            Repository root = repoFactory.firstRepository();
            Realm[] realms = new Realm[repoIds.length];
            for (int i = 0;i < realms.length; i++) {
                Repository repo = repoFactory.newRepository(repoIds[i]);
                if (repoIds[i].equals(root.getID())) {
                    // ROOT realm
                    realms[i] = realmConfig.getRootRealm();
                    //realms[i] = new Realm(repo.getName(), repo.getID(), new Path("/"));
                } else {
                    realms[i] = realmConfig.getRealm(repo.getID());
                    if (realms[i] == null) {
                        log.warn("No such realm defined: " + repo.getID() + " (fallback to repo configuration ...)");
                        realms[i] = new Realm(repo.getName(), repo.getID(), new Path("/" + repoIds[i] + "/"));
                    }
                }
            }
            return realms;
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    /**
     *
     */
    public Realm getRealm(Path path) {
        try {
            RepositoryFactory repoFactory = getRepositoryFactory();
            RepoPath rp = new YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), repoFactory);
            Realm realm = null;
            Repository root = repoFactory.firstRepository();
            if (rp.getRepo().getID().equals(root.getID())) {
                realm = realmConfig.getRootRealm();
                //realm = new Realm(rp.getRepo().getName(), rp.getRepo().getID(), new Path("/"));
            } else {
                realm = realmConfig.getRealm(rp.getRepo().getID());
                //realm = new Realm(rp.getRepo().getName(), rp.getRepo().getID(), new Path("/" + rp.getRepo().getID() + "/"));
            }
    
            //realm.setProxy(realmConfig.proxyHostName, realmConfig.proxyPort, realmConfig.proxyPrefix);
    
            return realm;
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }
}
