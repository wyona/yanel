package org.wyona.yanel.core.source;

import java.util.HashMap;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import org.apache.log4j.Category;
import org.wyona.yanel.core.Resource;

/**
 * Resolves a URI to a Source.
 * This class just checks the scheme and delegates to the scheme-specific resolver.
 * 
 * TODO: allow to configure schemes in a config file
 */
public class SourceResolver implements URIResolver {

    private static Category log = Category.getInstance(SourceResolver.class);
    
    private HashMap resolvers;
    private Resource resource;
    
    public SourceResolver(Resource resource) {
        this.resource = resource;
        this.resolvers = new HashMap();
    }
    
    public Source resolve(String uri, String base) throws SourceException {
        if (log.isDebugEnabled()) {
            log.debug("resolving: " + uri);
        }
        int colonIndex = uri.indexOf(":");
        if (colonIndex <= 0) {
            throw new SourceException("invalid url syntax (missing scheme): " + uri);
        }
        String scheme = uri.substring(0, colonIndex);
        URIResolver resolver = getResolver(scheme);
        if (resolver != null) {
            try {
                return resolver.resolve(uri, base);
            } catch (TransformerException e) {
                throw new SourceException(e.getMessage(), e);
            }
        } else {
            throw new SourceException("unknown scheme: " + scheme);
        }
    }
    
    private URIResolver getResolver(String scheme) {
        URIResolver resolver = null;
        if (this.resolvers.containsKey(scheme)) {
            resolver = (URIResolver)this.resolvers.get(scheme);
        } else {
            if (scheme.equals("yanelresource")) {
                resolver = new ResourceResolver(this.resource);
                this.resolvers.put(scheme, resolver);
            } else if (scheme.equals("yanelrepo")) {
                resolver = new YanelRepositoryResolver(this.resource);
                this.resolvers.put(scheme, resolver);
                //resolver = new RepositoryResolver(this.resource);
            } else if (scheme.equals("http")) {
                resolver = new HttpResolver(this.resource);
                this.resolvers.put(scheme, resolver);
            } else if (scheme.equals("rthtdocs")) {
                resolver = new RTHtdocsResolver(this.resource);
                this.resolvers.put(scheme, resolver);
            } else if (scheme.equals("rtyanelhtdocs")) {
                resolver = new RTYanelHtdocsResolver(this.resource);
                this.resolvers.put(scheme, resolver);
            }
        }
        return resolver;
    }
    
}
