/*
 * Copyright 2006 Wyona
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

package org.wyona.yanel.impl.resources;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.Topic;
import org.wyona.yanel.core.api.attributes.ModifiableV1;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;

import javax.servlet.http.HttpServletRequest;

import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.log4j.Category;

/**
 *
 */
public class ODTResource extends Resource implements ViewableV1, ModifiableV1 {

    private static Category log = Category.getInstance(ODTResource.class);

    /**
     *
     */
    public ODTResource() {
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        return null;
    }

    /**
     *
     */
    public View getView(Path path, String viewId) {
        View defaultView = new View();
        String mimeType = getMimeType(path, viewId);
        defaultView.setMimeType(mimeType);

        try {
            org.wyona.yarep.util.RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), new RepositoryFactory());

            if (mimeType.equals("application/xml")) {
                // Unzip ODT and return content of content.xml
                JarInputStream jarStream = new JarInputStream((rp.getRepo().getInputStream(new org.wyona.yarep.core.Path(rp.getPath().toString()))));
                JarEntry jarEntry;
                while ((jarEntry = jarStream.getNextJarEntry()) != null) {
                    log.debug("Jar Entry Name: " + jarEntry.getName());
                    if (jarEntry.getName().equals("content.xml")) {
                        defaultView.setInputStream(jarStream);
                        return defaultView;
                    }
                // TODO: What if zip does not contain a "content.xml"?!

                log.error("DEBUG: Config file: " + rtd.getConfigFile());
                }
            } else {
                log.debug("Mime-Type: " + mimeType);
                defaultView.setInputStream(rp.getRepo().getInputStream(new org.wyona.yarep.core.Path(rp.getPath().toString())));
            }
        } catch(Exception e) {
            log.error(e);
        }

        return defaultView;
    }

    /**
     *
     */
    private String getMimeType(Path path, String viewId) {
        String mimeType = null;
        try {
            // TODO: Get yanel RTI yarep properties file name from framework resp. use MapFactory ...!
            org.wyona.yarep.util.RepoPath rpRTI = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), new RepositoryFactory("yanel-rti-yarep.properties"));
            java.io.BufferedReader br = new java.io.BufferedReader(rpRTI.getRepo().getReader(new org.wyona.yarep.core.Path(new Path(rpRTI.getPath().toString()).getRTIPath().toString())));
            br.readLine();
            mimeType = br.readLine();
            if (mimeType != null) {
                if (mimeType.indexOf("mime-type:") == 0) {
                    mimeType = mimeType.substring(11);
                    log.info("*" + mimeType + "*");
                    // TODO: Maybe validate mime-type ...
                    return mimeType;
                }
            }
        } catch(Exception e) {
            log.warn(e);
        }

        String suffix = path.getSuffix();
        if (suffix != null) {
            log.debug("SUFFIX: " + suffix);
            if (suffix.equals("html")) {
                mimeType = "text/html";
            } else if (suffix.equals("xhtml")) {
                mimeType = "application/xhtml+xml";
            } else if (suffix.equals("xml")) {
                mimeType = "application/xml";
            } else {
                mimeType = "application/vnd.oasis.opendocument.text";
            }
        } else {
            mimeType = "application/vnd.oasis.opendocument.text";
        }
        return mimeType;
    }

    /**
     *
     */
    public View getView(HttpServletRequest request, String viewId) {
        return getView(new Path(request.getServletPath()), viewId);
    }

    /**
     *
     */
    public Reader getReader(Path path) {
        try {
            org.wyona.yarep.util.RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), new RepositoryFactory());
            return rp.getRepo().getReader(new org.wyona.yarep.core.Path(rp.getPath().toString()));
        } catch(Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     *
     */
    public Reader getReader(Topic topic) {
        log.error("Not implemented yet!");
        return null;
    }

    /**
     *
     */
    public Writer getWriter(Path path) {
        log.error("Not implemented yet!");
        return null;
    }

    /**
     *
     */
    public Writer getWriter(Topic topic) {
        log.error("Not implemented yet!");
        return null;
    }

    /**
     *
     */
    public OutputStream getOutputStream(Path path) {
        try {
            org.wyona.yarep.util.RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), new RepositoryFactory());
            return rp.getRepo().getOutputStream(new org.wyona.yarep.core.Path(rp.getPath().toString()));
        } catch(Exception e) {
            log.error(e);
        }
        return null;
    }
}
