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
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.ResourceTypeIdentifier;
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
    }
    
    public void setRealmConfiguration(RealmConfiguration realmConfig) {
        this.realmConfig = realmConfig;
    }
    
    protected RepositoryFactory getRepositoryFactory() throws Exception {
        return Yanel.getInstance().getRepositoryFactory("RTIRepositoryFactory");
    }

    /**
     *
     */
    public String getUUID() {
        return "sugus";
    }

    /**
     * See James Clark's explanation on namespaces: http://www.jclark.com/xml/xmlns.htm
     * @deprecated
     */
    public String getResourceTypeIdentifier(Path path) {
        log.debug("Original path: " + path);

        try {
            Repository repo = getRealm(path.toString()).getRTIRepository();
            Path rPath = new Path(getPath(getRealm(path.toString()), path.toString()));
            log.debug("Repo Name: " + repo.getName());
            log.debug("New path: " + rPath);

            log.debug("Resource Configuration Path: " + rPath.getRCPath());
            if (repo.exists(rPath.getRCPath())) {
                return new ResourceConfiguration(repo.getInputStream(rPath.getRCPath())).getUniversalName();
            } else if (repo.exists(rPath.getRTIPath())) {
                log.warn("DEPRECATED: " + rPath);
                return new ResourceTypeIdentifier(repo.getReader(rPath.getRTIPath())).getUniversalName();
            } else {
                log.warn("TODO: Implement chain of responsibility ...");
                return "<{http://www.wyona.org/yanel/resource/1.0}file/>";
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * @deprecated
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
                        realms[i] = new Realm(repo.getName(), repo.getID(), "/" + repoIds[i] + "/");
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
     * @deprecated
     */
    public Realm getRealm(Path path) {
        log.warn("DEPRECATED");
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
    
    /* (non-Javadoc)
     * @see org.wyona.yanel.core.map.Map#getRealm(java.lang.String)
     * @param url URL of request but without servlet context
     */
    public Realm getRealm(String url) throws Exception {
        log.debug("URL: " + url);
        Realm[] realms = realmConfig.getRealms();
        
        for (int i=0; i<realms.length; i++) {
            log.debug("checking realm : " + realms[i].getID() + " with mountpoint: " + realms[i].getMountPoint().toString());
            if (url.startsWith(realms[i].getMountPoint().toString())) {
                log.debug("matched!");
                return realms[i];
            }
        }
        log.debug("nothing matched! - > root realm");
        return realmConfig.getRootRealm();
    }

    /**
     * Maps the given url to a path. This default implementation does a one-to-one mapping,
     * but removes the realm prefix (mount-point).
     * E.g. if the url is /yanel-website/foo/bar.html, it will return /foo/bar.html.
     * @param url URL of request but without servlet context
     */
    public String getPath(Realm realm, String url) throws Exception {
        log.debug("URL: " + url);

        String mountPoint = realm.getMountPoint().toString();
        if (!url.startsWith(mountPoint)) {
            throw new Exception("Cannot map url [" + url + "] to path because the url does not " + 
                    "belong to the given realm : " + realm.getID() + ": " + mountPoint);
        }
        return "/" + url.substring(mountPoint.length());
    }
}
