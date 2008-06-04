package org.wyona.yanel.core.source;

import java.util.HashMap;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import org.apache.log4j.Category;
import org.wyona.yanel.core.Resource;
import org.wyona.commons.io.PathUtil;

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
    
    /**
     *
     */
    public Source resolve(String uri, String base) throws SourceException {
        if (log.isDebugEnabled()) {
            log.debug("URI to be resolved: " + uri);
            log.debug("Base: "+ base);
        }

        // NOTE: One cannot use ":/" in order to find the protocol/scheme, because one can also specifiy the realm id and repository id, for example: yanelrepo:REALM_ID:REPO_ID:/foo/bar.gif
        int colonIndex = uri.indexOf(":");
        //int colonIndex = uri.indexOf(":/");
        String uriScheme = "";
        if (colonIndex <= 0) {//Check for scheme in URI, if true, then URI has no scheme
            //log.error("DEBUG: URI has no scheme: " + uri);
            if (base != null) {
                int colBaseIndex = base.indexOf(":");
                //int colBaseIndex = base.indexOf(":/");
                if(colBaseIndex <=0 ){//Check for scheme in Base
                    throw new SourceException("invalid url syntax (missing scheme): " + uri);//no scheme found in uri and base
                }else{//base contains scheme. Use base scheme for uri scheme
                    uriScheme = base.substring(0, colBaseIndex);
                    uri = PathUtil.concat(base,uri);                
                    //log.error("DEBUG: Use scheme of base: " + uriScheme + ", " + uri);
                }
            } else {
                log.error("Neither scheme for URI nor base specified for URI: " + uri);
                throw new SourceException("invalid url syntax (missing scheme): " + uri);//no scheme found in uri and base
            }
        } else {//uri contains scheme
            uriScheme = uri.substring(0, colonIndex);
            //log.error("DEBUG: URI has scheme: " + uriScheme + ", " + uri);
        }

        URIResolver resolver = getResolver(uriScheme);
        if (resolver != null) {
            try {
                // TODO: What shall be used as base?!
                Source s = resolver.resolve(uri, base);
                s.setSystemId(uri);
                return s;
            } catch (TransformerException e) {
                throw new SourceException(e.getMessage(), e);
            }
        } else {
            throw new SourceException("No resolver could be loaded for scheme: " + uriScheme);
        }
    }
    
    /**
     *
     */
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
