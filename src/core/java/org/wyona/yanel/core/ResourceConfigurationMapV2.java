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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import org.apache.log4j.Logger;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.util.WildcardMatcherHelper;

/**
 * Implements a Chain of Responsibility (http://en.wikipedia.org/wiki/Chain-of-responsibility_pattern) to get the path for the .yanel-rc file by looking up for a match of path in map.rc-map
 * 
 * the map file needs to be located at YOUR-RTI-REPO/map.rc-map (TODO: make this configurable via realm)
 * example of a map.rc-map which maps every request which ends with .html to a html.yanel-rc
 * (whereas in this example STAR means * which is not possible to use with a slash in javadoc)
 * <pre>
 * &lt;?xml version="1.0"?&gt;
 * &lt;rc-map&gt;
 *   &lt;matcher pattern"/STARSTAR.html" rcpath="/html.yanel-rc"/&gt;
 * &lt;/rc-map&gt;
 * </pre>
 */
public class ResourceConfigurationMapV2 {

    private static Logger log = Logger.getLogger(ResourceConfigurationMapV2.class);

    /**
     * Get path of resource configuration which matched the path pattern within the resource configuration map
     * @param realm Realm
     * @param path Path of request
     * @return String which contains the path of a rc file
     */
    public static String getRCPath(Realm realm, String path, String queryString) {
        //log.debug("Try to get resource configuration for request: " + path + ", " + queryString);

        InputStream rcMapIS = getRCMap(realm);
        if (rcMapIS == null) {
            log.warn("No resource configuration map seems to exists for realm: " + realm.getID());
            return null;
        }

        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            XMLStreamReader parser = factory.createXMLStreamReader(rcMapIS);
            while (true) {
                int event = parser.next();
                if (event == XMLStreamConstants.END_DOCUMENT) {
                    parser.close();
                    break;
                }
                if (event == XMLStreamConstants.START_ELEMENT) {
                    if (parser.getLocalName().equals("matcher")) {
                        String pattern = parser.getAttributeValue("", "pattern");
         
                        String pathToMatch = path;
                        if (queryString != null && pattern.indexOf("?") >= 0) {
                            log.warn("DEBUG: Also check/match query string: " + pattern + ", " + queryString);
                            pathToMatch = path + "?" + queryString;
                        }
                        if (WildcardMatcherHelper.match(pattern, pathToMatch) != null) {
                            if (log.isDebugEnabled()) {
                                log.debug("CoR pattern: '" + pattern + "' matched with path: '" + pathToMatch + "'. will use following path in the RTIRepository to reach the rc: " + parser.getAttributeValue("", "rcpath"));
                            }
                            return parser.getAttributeValue("", "rcpath");
                        }
                    } else {
                        //log.debug("No matcher element '" + parser.getLocalName() + "', hence ignore.");
                    }
                }
            }
        } catch (XMLStreamException e) {
            log.error("error while reading the rc map", e);
            return null;
        }
        return null;
    }

    /**
     * Get resource configuration map
     * @param realm Realm containing resource configuration map
     * @return InputStream of RTIRepository/map.rc-map
     */
    private static InputStream getRCMap(Realm realm) {
        try {
            //TODO: make this configurable via realm
            return realm.getRTIRepository().getNode("/map.rc-map").getInputStream();
        } catch (Exception e) {
            log.error("Could not get InputStream of rc-map",e);
            return null;
        }
    }
}
