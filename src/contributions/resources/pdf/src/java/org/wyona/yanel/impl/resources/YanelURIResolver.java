/*
 * Copyright 2007 Wyona
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

package org.wyona.yanel.impl.resources;

import javax.xml.transform.URIResolver;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.net.URL;
import java.io.InputStream;
import java.util.StringTokenizer;

import org.apache.log4j.Category;

import org.wyona.yanel.core.Resource;

import org.wyona.yarep.core.RepositoryException;
import org.wyona.yarep.core.Repository;

/**
 * URI resolver for the scheme "yanel"
 */
public class YanelURIResolver implements URIResolver {
    private static Category log = Category.getInstance(YanelURIResolver.class);
    
    private static final String SCHEME = "yanel";
    
    private Resource resource;
    
    public YanelURIResolver(Resource resource) {
        this.resource = resource;
    }

    public Source resolve(String href, String base) {
        if (log.isDebugEnabled()) {
            log.debug("resolving: " + href);
        }
    	
        String prefix = SCHEME + ":/";
    	
        // only accept 'yanel:/' URIs
        if (href == null || !href.startsWith(prefix)){
            return null;
        }

        // we can't resolve to a Collection (indicated by a trailing '/')
        if (href.charAt(href.length() - 1) == '/'){
            return null;
        }

        StringTokenizer tokens = new StringTokenizer(href.substring(7), "/");
        if (!tokens.hasMoreTokens()){
            return null; // nothing after the 'yanel:/'
        }
	    
        try {
            String path = href.substring(7); // truncate yanel:/
            Repository repo = resource.getRealm().getRepository();
            InputStream in = repo.getNode(path).getInputStream();
            return new StreamSource(in);
        } catch (Exception e) {
        	String errorMsg = "Could not resolve URI: " + href + ": " + e.toString();
            log.error(errorMsg, e);
        }
        return null;
    }
}
