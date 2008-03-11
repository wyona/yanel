package org.wyona.yanel.core.source;

import java.io.File;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.wyona.yanel.core.Resource;


/**
 * Resolves URIs which point to a node in a repository of a yanel realm.
 * 
 * Syntax:
 * rthtdocs:{path}
 * 
 * e.g.
 * rthtdocs:/foo/bar.xml
 * 
 * TODO: support access to other resource types (e.g. rthtdocs:resourcTypeIdentifier:/foo/bar.xml) 
 * 
 */
public class RTHtdocsResolver implements URIResolver {

    private static Logger log = Logger.getLogger(RTHtdocsResolver.class);
    private static final String SCHEME = "rthtdocs";
    private Resource resource;
    
    public RTHtdocsResolver(Resource resource) {
        this.resource = resource;
    }

    public Source resolve(String href, String base) throws SourceException {
        String prefix = SCHEME + ":";
        // only accept 'rthtdocs:' URIs
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
            InputStream in = resource.getClass().getClassLoader().getResource(packageName.replace('.','/') + "/htdocs" + path).openStream();
            return new StreamSource(in);
        } catch (Exception e) {
            try {
                File resourceFile = new File(resource.getRTD().getConfigFile().getParentFile().getAbsolutePath() + "/htdocs" + path.replace('/', File.separatorChar));
                InputStream in = new java.io.FileInputStream(resourceFile);
                return new StreamSource(in);
            } catch (Exception ex) {
                String errorMsg = "Could not resolve URI: " + path + ": " + e.toString();
                log.error(errorMsg, e);
                throw new SourceException(errorMsg, e);
            }
        }
    }
}
