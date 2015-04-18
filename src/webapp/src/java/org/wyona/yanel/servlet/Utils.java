/*
 * See the NOTICE.txt file distributed with
 * this work for additional information regarding copyright ownership.
 * Wyona licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wyona.yanel.servlet;

import java.net.URL;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.map.ReverseProxyConfig;

import javax.servlet.http.HttpServletRequest;

/**
 * Utilities
 */
public class Utils {

    private static Logger log = LogManager.getLogger(Utils.class);

    /**
     * Patch request with proxy settings re realm configuration
     * @param realm Realm associated with request
     * @param request Request which Yanel received
     * @param addQS Additonal query string
     * @param xml Flag whether returned URL should be XML compatible, e.g. re ampersands
     * @return URL which was received by reverse proxy, e.g. http://www.yanel.org/en/download/index.html instead http://127.0.0.1:8080/yanel/yanel-website/en/download/index.html
     */
    public static String getRequestURLQS(Realm realm, HttpServletRequest request, String addQS, boolean xml) {
        try {
            // TODO: Handle this exception more gracefully!
            if (realm == null) log.error("No realm found for path " +request.getServletPath());

            String proxyHostName = realm.getProxyHostName();
            int proxyPort = realm.getProxyPort();
            String proxyPrefix = realm.getProxyPrefix();
            ReverseProxyConfig reverseProxyConfig = realm.getReverseProxyConfig();
            log.warn("DEBUG BILLY: Hostname: " + realm.getProxyHostName() + ", " + reverseProxyConfig.getHostName());

            URL url = new URL(request.getRequestURL().toString());

            if(realm.isProxySet()) {
                String forwardedHost = request.getHeader("X-FORWARDED-HOST");
                if (forwardedHost != null) {
                    url = new java.net.URL(url.getProtocol(), forwardedHost, url.getFile());
                }
                if (proxyHostName != null) {
                    url = new URL(url.getProtocol(), proxyHostName, url.getFile());
                }

                if (proxyPort >= 0) {
                    //log.debug("Configured proxy port: " + proxyPort);
                    url = new URL(url.getProtocol(), url.getHost(), proxyPort, url.getFile());
                } else {
                    //log.debug("No proxy port configured, hence use default port: " + url.getDefaultPort());
                    url = new URL(url.getProtocol(), url.getHost(), url.getFile()); // INFO: Please note that if one does not set the port explicitely, then toString() won't add the port to the returned string.
                }

                if (proxyPrefix != null) {
                    // INFO: Remove proxy prefix, e.g. "/yanel/yanel-website"
                    url = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile().substring(proxyPrefix.length()));
                }

                if (reverseProxyConfig.getReversePrefix() != null) {
                    // INFO: Add reverse proxy prefix, e.g. "/boost2"
                    url = new URL(url.getProtocol(), url.getHost(), url.getPort(), reverseProxyConfig.getReversePrefix() + url.getFile());
                }

                //log.debug("Proxy enabled for this realm resp. request: " + realm + ", " + url);
            } else {
                //log.debug("No proxy set for this realm resp. request: " + realm + ", " + url);
            }

            String urlQS = url.toString();
            if (request.getQueryString() != null) {
                urlQS = urlQS + "?" + request.getQueryString();
                if (addQS != null) urlQS = urlQS + "&" + addQS;
            } else {
                if (addQS != null) urlQS = urlQS + "?" + addQS;
            }
   
            if (xml) {
                urlQS = urlQS.replaceAll("&", "&amp;");
            }
   
            if(log.isDebugEnabled()) log.debug("Request: " + urlQS);

            return urlQS;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
