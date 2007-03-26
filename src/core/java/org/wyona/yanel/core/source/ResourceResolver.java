package org.wyona.yanel.core.source;

import org.apache.log4j.Category;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceManager;
import org.wyona.yanel.core.Yanel;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.util.ResourceAttributeHelper;
import org.xml.sax.InputSource;

/**
 * Source resolver for yanel resources.
 * It returns an InputSource made from the view of the resource with
 * the given uri. It will look for the resource in the realm of the resource
 * which is passed to the constructor of this ResourceResolver.
 * 
 * It uses the following URI syntax:
 * yanelresource:/path/to/resource
 * 
 * TODO: support relative uris?
 */
public class ResourceResolver implements SourceResolver {

    private static Category log = Category.getInstance(ResourceResolver.class);
    
    private static final String SCHEME = "yanelresource";

    private Resource resource;
    
    public ResourceResolver(Resource resource) {
        this.resource = resource;
    }
    
    /**
     * @see org.wyona.yanel.core.source.SourceResolver#resolve(java.lang.String)
     */
    public InputSource resolve(String uri) throws SourceException {
        log.debug("resolving: " + uri);
        String prefix = SCHEME + ":";
        if (!uri.startsWith(prefix)) {
            log.error("unknown scheme: " + uri);
            return null;
        }
        try {
            uri = uri.substring(prefix.length());
            ResourceManager manager = Yanel.getInstance().getResourceManager();
            Resource targetResource = manager.getResource(resource.getRequest(), resource.getResponse(), 
                    resource.getRealm(), uri);
            if (ResourceAttributeHelper.hasAttributeImplemented(targetResource, "Viewable", "1")) {
                String viewV1path = resource.getRealm().getMountPoint() + uri.substring(1);
                log.debug("including document: " + viewV1path);
                View view = ((ViewableV1) targetResource).getView(new Path(viewV1path), null);
                return new InputSource(view.getInputStream());
            } else if (ResourceAttributeHelper.hasAttributeImplemented(targetResource, "Viewable", "2")) {
                View view = ((ViewableV2) targetResource).getView(null);
                return new InputSource(view.getInputStream());
            } else {
                log.warn("Resource is not viewable: " + uri);
                return null;
            }
        } catch (Exception e) {
            String errorMsg = "Could not resolve URI: " + uri + ": " + e.toString();
            log.error(errorMsg, e);
            throw new SourceException(errorMsg, e);
        }
    }
    
}
