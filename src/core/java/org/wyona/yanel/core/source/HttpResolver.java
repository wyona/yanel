package org.wyona.yanel.core.source;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Category;
import org.wyona.yanel.core.Resource;


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

    private static final String HTTP_SCHEME = "http";
    private static final String HTTPS_SCHEME = "https";
    
    //private Resource resource;
    
    public HttpResolver(Resource resource) {
        //this.resource = resource;
    }

    /**
     * @see javax.xml.transform.URIResolver#resolve(String, String)
     */
    public Source resolve(String href, String base) throws SourceException {
        String httpPrefix = HTTP_SCHEME + ":";
        String httpsPrefix = HTTPS_SCHEME + ":";
        
        // INFO: Only accept URIs which start either with 'http:' or 'https:'
        if (href != null && (href.startsWith(httpPrefix) || href.startsWith(httpsPrefix))){
            // INFO: href seems to start with the correct scheme
        } else {
            log.error("Href '" + href + "' does neither start with prefix '" + httpPrefix + "' nor with '" + httpsPrefix + "'!");
            return null;
        }

        try {
            java.net.URL url = new java.net.URL(href);
            if (log.isDebugEnabled()) log.debug("Resolve: " + url.toString());
            // TODO: Support SSL ...
            return new StreamSource(url.openConnection().getInputStream());
        } catch (Exception e) {
            String errorMsg = "Could not resolve URI: " + href + ": " + e.toString();
            log.error(errorMsg, e);
            throw new SourceException(errorMsg, e);
        }
    }
}
