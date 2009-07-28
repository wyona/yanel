package org.wyona.yanel.core.source;

import org.wyona.yanel.core.Resource;


/**
 * Resolves URIs which point to a node in a repository of a yanel realm.
 * 
 * Syntax:
 * rtyanelhtdocs:{path}
 * 
 * e.g.
 * rtyanelhtdocs:/foo/bar.xml
 */
public class RTYanelHtdocsResolver extends RTabstractResolver {

    private static final String SCHEME = "rtyanelhtdocs";
    private static final String PATH_PREFIX = "yanel-htdocs";

    public RTYanelHtdocsResolver(Resource resource) {
        super(resource);
    }

    @Override
    protected String getScheme() { return SCHEME; }

    @Override
    protected String getPathPrefix() { return PATH_PREFIX; }
}
