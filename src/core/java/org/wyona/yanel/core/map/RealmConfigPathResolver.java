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

import java.io.File;

import org.wyona.commons.io.FileUtil;
import org.apache.log4j.Logger;

/**
 *
 */
public class RealmConfigPathResolver implements javax.xml.transform.URIResolver {

    private File realmConfigFile;

    private static Logger log = Logger.getLogger(RealmConfigPathResolver.class);

    /**
     *
     */
    public RealmConfigPathResolver(Realm realm) {
        realmConfigFile = realm.getConfigFile();
    }

    /**
     *
     */
    public javax.xml.transform.Source resolve(String href, String base) throws javax.xml.transform.TransformerException {
        if (href.indexOf(":") > 0) {
            log.error("No such scheme implemented: " + href.substring(0, href.indexOf(":")) + " (InputStream will not be set!)");
            javax.xml.transform.stream.StreamSource source = new javax.xml.transform.stream.StreamSource();
            source.setSystemId(href);
            return source;
        } 
        File file = FileUtil.resolve(realmConfigFile, new File(href));
        javax.xml.transform.stream.StreamSource source = new javax.xml.transform.stream.StreamSource(file);
        try {
            source.setInputStream(new java.io.FileInputStream(file));
        } catch (Exception e) {
            log.error(e, e);
            new javax.xml.transform.TransformerException(e.getMessage());
        }
        source.setSystemId(file.toString());
        return source;
    }
}
