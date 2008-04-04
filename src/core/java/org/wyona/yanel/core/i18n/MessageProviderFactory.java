/*
 * Copyright 2008 Wyona
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.wyona.org/licenses/APACHE-LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wyona.yanel.core.i18n;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;

import org.wyona.yanel.core.source.SourceException;
import org.wyona.yanel.core.source.YanelStreamSource;

/**
 * A MessageProviderFactory creates MessageProviders. It supports two different
 * providers, ResourceBundleMessageProvider and XMLMessageProvider.
 * It supports caching of XMLMessageProviders.
 */
public class MessageProviderFactory {
    protected static HashMap cache = new HashMap();

    /**
     * Gets a message provider for the given catalogue. If the catalogue name is
     * a URI, the catalogue is assumed to be an XML catalogue and an
     * XMLMessageProvider will be instantiated. In this case the catalogue will
     * be resolved by the resolver and the message provider will be cached if
     * possible. Otherwise, if the catalogue name is not a URI, the catalogue is
     * assumed to be a resource bundle catalogue and a
     * ResourceBundleMessageProvider will be instantiated. 
     * A ResourceBundleMessageProvider will not be cached.
     * 
     * @param catalogue
     * @param resolver
     * @return message provider
     * @throws SourceException
     */
    public static synchronized MessageProvider getMessageProvider(String catalogue,
            URIResolver resolver) throws SourceException {
        MessageProvider messageProvider = null;

        if (catalogue.indexOf(":") == -1) {
            messageProvider = getResourceBundleMessageProvider(catalogue);
        } else {
            messageProvider = getXMLMessageProvider(catalogue, resolver);
        }

        return messageProvider;
    }

    public static synchronized MessageProvider getResourceBundleMessageProvider(String catalogue) {
        return new ResourceBundleMessageProvider(catalogue);
    }

    public static synchronized MessageProvider getXMLMessageProvider(String catalogue,
            URIResolver resolver) throws SourceException {
        try {
            Source source = resolver.resolve(catalogue, null);
            long sourceLastModified = -1;

            if (source instanceof YanelStreamSource) {
                sourceLastModified = ((YanelStreamSource) source).getLastModified();
            }

            if (cache.containsKey(catalogue) && sourceLastModified != -1) {
                // message provider is in the cache, verify validity
                MessageProviderCacheEntry entry = (MessageProviderCacheEntry) cache.get(catalogue);
                long cacheLastModified = entry.lastModified;

                if (cacheLastModified >= sourceLastModified) {
                    // cached message provider is valid
                    return entry.messageProvider;
                }
            }

            InputStream is = SAXSource.sourceToInputSource(source).getByteStream();
            XMLMessageProvider messageProvider = new XMLMessageProvider(is);

            if (sourceLastModified != -1) {
                MessageProviderCacheEntry cacheEntry = new MessageProviderCacheEntry(
                        messageProvider, sourceLastModified);
                cache.put(catalogue, cacheEntry);
            }

            return messageProvider;

        } catch (TransformerException e) {
            throw new SourceException(e.getMessage(), e);
        }
    }

    static class MessageProviderCacheEntry {
        MessageProvider messageProvider;

        long lastModified;

        MessageProviderCacheEntry(MessageProvider messageProvider, long lastModified) {
            this.messageProvider = messageProvider;
            this.lastModified = lastModified;
        }
    }
}