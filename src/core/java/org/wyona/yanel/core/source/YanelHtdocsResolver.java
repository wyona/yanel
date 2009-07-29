package org.wyona.yanel.core.source;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import org.apache.log4j.Logger;
import org.wyona.yanel.core.Resource;

public class YanelHtdocsResolver implements URIResolver {

    private static Logger log = Logger.getLogger(YanelHtdocsResolver.class);

    private static final String SCHEME = "yanelhtdocs";
    private static final String PATH_PREFIX = "htdocs";
    private Resource resource;
    
    public YanelHtdocsResolver(Resource resource) {
        this.resource = resource;
    }

    protected String getScheme() { return SCHEME; }

    public Source resolve(String href, String base) throws TransformerException {
        String prefix = getScheme() + ":";
        // only accept '<scheme>:' URIs
        if (href == null || !href.startsWith(prefix)){
            return null;
        }
        // we can't resolve to a Collection (indicated by a trailing '/')
        if (href.endsWith("/")){
            return null;
        }
        String path = href.substring(prefix.length());

        HttpServletRequest request = resource.getEnvironment().getRequest();

        //XXX HACK: we have no other way to have access to this directory from the Yanel environment alone:
        String localFilePath = request.getRealPath("/" + PATH_PREFIX + path);
        log.fatal("localFilePath: "+localFilePath);
        File resourceFile = new File(localFilePath);
        //return new YanelStreamSource(resourceFile);
        InputStream in;
        try {
            in = new java.io.FileInputStream(resourceFile);
        } catch (FileNotFoundException e) {
            throw new SourceException(e.getMessage(), e);
        }
        YanelStreamSource source = new YanelStreamSource(in);
        long resourceLastModified = resourceFile.lastModified();
        source.setLastModified(resourceLastModified);
        return source;
        /*TODO REMOVEME does not work with HTTPS, credential problems...
        StringBuffer sb = request.getRequestURL();
        log.fatal("sb: "+sb);
        String globalHtdocsPath = PathUtil.getGlobalHtdocsPath(resource);
        log.fatal("globalHtdocsPath: "+globalHtdocsPath);
        try {
            URL url = new URL(sb.toString() + globalHtdocsPath + path);
            if (log.isDebugEnabled()) log.debug("Resolve: " + url.toString());
            return new StreamSource(url.openConnection().getInputStream());
        } catch (Exception e) {
            String errorMsg = "Could not resolve URI: <" + href + ">: " + e.toString();
            throw new SourceException(errorMsg, e);
        }
        */
        /*TODO REMOVEME does not work, the reserved area is indeed not handled by a resource-type!
        String reservedPrefix = resource.getYanel().getReservedPrefix();
        return resolver.resolve("yanelresource:" + "/" + reservedPrefix + path, base);
        */
    }

}
