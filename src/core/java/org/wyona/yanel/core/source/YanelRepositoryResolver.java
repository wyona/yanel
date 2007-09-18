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
 * Resolves URIs which point to a node in a repository of a yanel realm.
 * 
 * Syntax:
 * yanelrepo:{realmID}:{repoID}:{path}
 * yanelrepo:{realmID}:{path}
 * yanelrepo:{path}
 * 
 * e.g.
 * yanelrepo:myrealm:myrepo:/foo/bar.xml
 * yanelrepo:myrealm:/foo/bar.xml
 * yanelrepo:/foo/bar.xml
 * 
 */
public class YanelRepositoryResolver implements URIResolver {

    private static Category log = Category.getInstance(YanelRepositoryResolver.class);

    private static final String SCHEME = "yanelrepo";
    
    private Resource resource;
    
    public YanelRepositoryResolver(Resource resource) {
        this.resource = resource;
    }

    public Source resolve(String href, String base) throws SourceException {
        String prefix = SCHEME + ":";
        
        // only accept 'yanelrepo:' URIs
        if (href == null || !href.startsWith(prefix)){
            return null;
        }

        String[] tokens = href.split(":");
        String realmID = null;
        String repoID = null;
        String path = null;
        if (tokens.length == 2) {
            path = tokens[1];
        } else if (tokens.length == 3) {
            realmID = tokens[1];
            path = tokens[2];
        } else if (tokens.length == 4) {
            realmID = tokens[1];
            repoID = tokens[2];
            path = tokens[3];
        } else {
            throw new SourceException("invalid url syntax: " + href);
        }
        
        // we can't resolve to a Collection (indicated by a trailing '/')
        if (path.endsWith("/")){
            return null;
        }

        try {
            Realm realm;
            if (realmID != null) {
                realm = Yanel.getInstance().getRealmConfiguration().getRealm(realmID);
            } else {
                realm = resource.getRealm();
            }
            Repository repo;
            if (repoID != null) {
                repo = realm.getRepository(repoID);
            } else {
                repo = realm.getRepository();
            }
            InputStream in = repo.getNode(path).getInputStream();
            return new StreamSource(in);
        } catch (Exception e) {
            String errorMsg = "Could not resolve URI: " + href + ": " + e.toString();
            log.error(errorMsg, e);
            throw new SourceException(errorMsg, e);
        }
    }
}
