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

package org.wyona.yanel.core;

import java.io.InputStream;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.util.WildcardMatcherHelper;

/**
 * Implements a Chain of Responsebility to get the path for the .yanel-rc file by looking up for a
 * match of path in sitemap.rc-map
 */
public class ResourceConfigurationMap {

    public static String getRCPath(Realm realm, String path) {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            XMLStreamReader parser = factory.createXMLStreamReader(getRCMap(realm, path));
            while (true) {
                int event = parser.next();
                if (event == XMLStreamConstants.END_DOCUMENT) {
                    parser.close();
                    break;
                }
                if (event == XMLStreamConstants.START_ELEMENT) {
                    if (parser.getLocalName().equals("matcher")) {
                        String pattern = parser.getAttributeValue("", "pattern");
                        if (WildcardMatcherHelper.match(pattern, path) != null) {
                            return parser.getAttributeValue("", "rcpath");
                        }
                    }
                }
            }
        } catch (Exception a) {
            return null;
        }
        return null;
    }

    /**
     * 
     */
    private static String getRCMapPath(String path) {
        // Remove trailing slash except for ROOT ...
        if (path.length() > 1 && path.charAt(path.length() - 1) == '/') {
            return path.substring(0, path.length() - 1) + ".rc-map";
        }
        return path + ".rc-map";
    }

    /**
     * 
     */
    private static InputStream getRCMap(Realm realm, String path) throws Exception {
        return realm.getRTIRepository().getInputStream(new Path(getRCMapPath("/map")));
    }
}
