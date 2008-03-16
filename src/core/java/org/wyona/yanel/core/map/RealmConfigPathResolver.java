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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.wyona.commons.io.FileUtil;
import org.wyona.commons.io.Path;
import org.wyona.security.core.IdentityManagerFactory;
import org.wyona.security.core.PolicyManagerFactory;
import org.wyona.security.core.api.IdentityManager;
import org.wyona.security.core.api.PolicyManager;
import org.wyona.yanel.core.LanguageHandler;
import org.wyona.yanel.core.Yanel;
import org.wyona.yanel.core.attributes.translatable.DefaultTranslationManager;
import org.wyona.yanel.core.attributes.translatable.TranslationManager;
import org.wyona.yanel.core.api.security.WebAuthenticator;
import org.wyona.yanel.core.util.ConfigurationUtil;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.xml.sax.SAXException;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

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
        javax.xml.transform.Source source = new javax.xml.transform.stream.StreamSource();
        source.setSystemId(FileUtil.resolve(realmConfigFile, new File(href)).toString());
        return source;
    }
}
