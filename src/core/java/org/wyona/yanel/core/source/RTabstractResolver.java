package org.wyona.yanel.core.source;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import org.apache.log4j.Logger;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceTypeDefinition;
import org.wyona.yanel.core.Yanel;


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
        int prefixLength = prefix.length();
        ClassLoader resourceClassLoader = resource.getClass().getClassLoader();

        String path;
        Class<?> clazz;
        String name;
        ResourceTypeDefinition rtd;

        int RTendIndex = href.indexOf(":/", prefixLength);
        if (RTendIndex == -1) {
            path = href.substring(prefixLength);
            clazz = resource.getClass();
            name = resource.getResourceTypeUniversalName();
            rtd = resource.getRTD();

        } else {
            path = href.substring(RTendIndex + 1);
            String[] URLencodedNSURI_and_localName = href.substring(prefixLength, RTendIndex).split("::", 2);
            try {
                String NSURI = URLDecoder.decode(URLencodedNSURI_and_localName[0], "UTF-8");
                String localName = URLencodedNSURI_and_localName[1];
                String universalName = "<{" + NSURI + "}" + localName + "/>";
                Yanel yanel = Yanel.getInstance();
                rtd = yanel.getResourceTypeRegistry().getResourceTypeDefinition(universalName);
                String className = rtd.getResourceTypeClassname();
                clazz = resourceClassLoader.loadClass(className); //XXX(?): is this really the classloader we want to use? 
                name = rtd.getResourceTypeUniversalName();
            } catch (Exception e) {
                String errorMsg = "Could not resolve URI: " + path + ": " + e.getMessage();
                log.error(errorMsg, e);
                throw new SourceException(errorMsg, e);
            }
        }
        try {
            String fullyQualifiedName = clazz.getName();
            int lastDot = fullyQualifiedName.lastIndexOf ('.');
            String packageName = fullyQualifiedName.substring (0, lastDot);
            if (log.isDebugEnabled()) {
                log.debug("Package: " + packageName);
            }
            URL url = clazz.getClassLoader().getResource(packageName.replace('.','/') + "/" + getPathPrefix() + path);
            if (url == null) {
                log.info("Path " + getPathPrefix() + path + " does not seem to be contained within package " + packageName + " of resource " + name + ". Check within resource config location ...");
            }

            // If url == null, then url.openStream() will throw an exception and the fallback will be used within the catch below (TODO: Refactor ...)
            InputStream in = url.openStream();
            YanelStreamSource source = new YanelStreamSource(in);
            URLConnection uc = url.openConnection();
            long resourceLastModifier = uc.getLastModified();
            source.setLastModified(resourceLastModifier);
            return source;
        } catch (Exception e) {
            File resourceConfigDir = rtd.getConfigFile().getParentFile();
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
