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
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Category;

/**
 *
 */
public class FileResource extends Resource implements ViewableV1 {

    private static Category log = Category.getInstance(FileResource.class);

    /**
     *
     */
    public FileResource() {
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
        try {
            org.wyona.yarep.util.RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), new RepositoryFactory());
            defaultView.setInputStream(rp.getRepo().getInputStream(new org.wyona.yarep.core.Path(rp.getPath().toString())));

            // TODO: Get repo suffix of RTIs from framework ... (also see MapImpl)
            java.io.BufferedReader br = new java.io.BufferedReader(rp.getRepo().getReader(new org.wyona.yarep.core.Path(rp.getPath().toString() + ".yanel-rti")));
            br.readLine();
            String mimeType = br.readLine();
            if (mimeType != null) {
                if (mimeType.indexOf("mime-type:") == 0) {
                    mimeType = mimeType.substring(11);
                    log.info("*" + mimeType + "*");
                    // TODO: Maybe validate mime-type based on mime.types config ...
                    defaultView.setMimeType(mimeType);
                    return defaultView;
                }
            }

            // TODO: Load config mime.types ...
            String suffix = path.getSuffix();
            if (suffix != null) {
                log.debug("SUFFIX: " + suffix);
                if (suffix.equals("html")) {
                    defaultView.setMimeType("text/html");
	        } else if (suffix.equals("xhtml")) {
                    defaultView.setMimeType("application/xhtml+xml");
	        } else if (suffix.equals("xml")) {
                    defaultView.setMimeType("application/xml");
	        } else if (suffix.equals("css")) {
                    defaultView.setMimeType("text/css");
	        } else if (suffix.equals("png")) {
                    defaultView.setMimeType("image/png");
	        } else if (suffix.equals("jpg")) {
                    defaultView.setMimeType("image/jpeg");
	        } else if (suffix.equals("gif")) {
                    defaultView.setMimeType("image/gif");
	        } else if (suffix.equals("pdf")) {
                    defaultView.setMimeType("application/pdf");
	        } else if (suffix.equals("doc")) {
                    defaultView.setMimeType("application/msword");
	        } else if (suffix.equals("odt")) {
                    defaultView.setMimeType("application/vnd.oasis.opendocument.text");
	        } else if (suffix.equals("sxc")) {
                    defaultView.setMimeType("application/vnd.sun.xml.calc");
                } else {
                    defaultView.setMimeType("application/octet-stream");
                }
            } else {
                log.warn("mime-type will be set to application/octet-stream, because no suffix for " + path);
                defaultView.setMimeType("application/octet-stream");
            }

        } catch(Exception e) {
            log.error(e);
        }

        return defaultView;
    }

    /**
     *
     */
    public View getView(HttpServletRequest request, String viewId) {
        return getView(new Path(request.getServletPath()), viewId);
    }
}
