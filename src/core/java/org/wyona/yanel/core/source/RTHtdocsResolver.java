package org.wyona.yanel.core.source;

import org.wyona.yanel.core.Resource;


/**
 * Resolves URIs which point to a node in a repository of a yanel realm.
 * 
 * Syntax:
 * rthtdocs:{path}
 * 
 * e.g.
 * rthtdocs:/foo/bar.xml
 */
public class RTHtdocsResolver extends RTabstractResolver {

    private static final String SCHEME = "rthtdocs";
    private static final String PATH_PREFIX = "htdocs";

    public RTHtdocsResolver(Resource resource) {
        super(resource);
    }

    @Override
    protected String getScheme() { return SCHEME; }

    @Override
    protected String getPathPrefix() { return PATH_PREFIX; }
}