package org.wyona.yanel.core.source;

import org.xml.sax.InputSource;

/**
 * Resolves a URI to a InputSource.
 * TODO: make this more generic, i.e. return a more generic source object.
 */
public interface SourceResolver {

    InputSource resolve(String uri) throws SourceException;
    
}
