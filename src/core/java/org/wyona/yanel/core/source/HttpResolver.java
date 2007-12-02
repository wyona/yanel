package org.wyona.yanel.core.source;

import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Category;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.Yanel;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yarep.core.Repository;


/**
 * Resolves URIs with scheme "http".
 * 
 * Syntax:
 * http:{path}
 * 
 * e.g.
 * http://foo/bar.xml
 * 
 */
public class HttpResolver implements URIResolver {

    private static Category log = Category.getInstance(HttpResolver.class);

    private static final String SCHEME = "http";
    
    private Resource resource;
    
    public HttpResolver(Resource resource) {
        this.resource = resource;
    }

    public Source resolve(String href, String base) throws SourceException {
        String prefix = SCHEME + ":";
        
        // only accept 'http:' URIs
        if (href == null || !href.startsWith(prefix)){
            return null;
        }

        try {
            java.net.URL url = new java.net.URL(href);
            if (log.isDebugEnabled()) log.debug("Resolve: " + url.toString());
            return new StreamSource(url.openConnection().getInputStream());
        } catch (Exception e) {
            String errorMsg = "Could not resolve URI: " + href + ": " + e.toString();
            log.error(errorMsg, e);
            throw new SourceException(errorMsg, e);
        }
    }
}
