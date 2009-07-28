package org.wyona.yanel.core.source;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import org.apache.log4j.Logger;
import org.wyona.yanel.core.Resource;


/**
 * TODO: support access to other resource types (e.g. rthtdocs:resourcTypeIdentifier:/foo/bar.xml) 
 */
public abstract class RTabstractResolver implements URIResolver {

    private static Logger log = Logger.getLogger(RTabstractResolver.class);
    private Resource resource;

    public RTabstractResolver(Resource resource) {
        this.resource = resource;
    }

    protected abstract String getScheme();
    protected abstract String getPathPrefix();

    public Source resolve(String href, String base) throws SourceException {
        String prefix = getScheme() + ":";
        // only accept '<scheme>:' URIs
        if (href == null || !href.startsWith(prefix)) {
            return null;
        }
        // we can't resolve to a Collection (indicated by a trailing '/')
        if (href.endsWith("/")){
            return null;
        }
        String path = href.substring(prefix.length());
        try {
            String fullyQualifiedName = resource.getClass().getName();
            int lastDot = fullyQualifiedName.lastIndexOf ('.');
            String packageName = fullyQualifiedName.substring (0, lastDot);
            if (log.isDebugEnabled()) {
                log.debug("Package: " + packageName);
            }
            URL url = resource.getClass().getClassLoader().getResource(packageName.replace('.','/') + "/" + getPathPrefix() + path);
            if (url == null) {
                log.error("Path " + getPathPrefix() + path + " does not seem to be contained within package " + packageName + " of resource " + resource.getResourceTypeUniversalName());
            }
            InputStream in = url.openStream();
            YanelStreamSource source = new YanelStreamSource(in);
            URLConnection uc = url.openConnection();
            long resourceLastModifier = uc.getLastModified();
            source.setLastModified(resourceLastModifier);
            return source;
        } catch (Exception e) {
            File resourceConfigDir = resource.getRTD().getConfigFile().getParentFile();
            log.info("Fallback to resource config location: " + resourceConfigDir);
            try {
                File resourceFile = new File(resourceConfigDir.getAbsolutePath() + "/" + getPathPrefix() + path.replace('/', File.separatorChar));
                InputStream in = new java.io.FileInputStream(resourceFile);
                YanelStreamSource source = new YanelStreamSource(in);
                long resourceLastModified = resourceFile.lastModified();
                source.setLastModified(resourceLastModified);
                return source;
            } catch (Exception ex) {
                //FIXME(?): ex is never logged or thrown
                // but the previously caught e is used instead?!?
                String errorMsg = "Could not resolve URI: " + path + " (" + resourceConfigDir + ")" + ": " + e.toString();
                log.error(errorMsg, e);
                throw new SourceException(errorMsg, e);
            }
        }
    }
}
