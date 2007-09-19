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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Category;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.source.SourceResolver;
import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yarep.util.RepoPath;
import org.wyona.commons.io.PathUtil;

/**
 *
 */
public class ZipResource extends Resource implements ViewableV2 {

    private static Category log = Category.getInstance(ZipResource.class);

    public ZipResource() {
    }

    public ViewDescriptor[] getViewDescriptors() {
        return null;
    }

    public View getView(String viewId) {
        View defaultView = new View();
        try {
            Repository contentRepo = getRealm().getRepository();
            String zipPath;
            if (getResourceConfigProperty("zip-dir") != null) {
                // zip-dir is only for historical reasons
                zipPath = getResourceConfigProperty("zip-dir");
            } else {
                zipPath = getResourceConfigProperty("zip-path");
            }
            if (log.isDebugEnabled()) {
                log.debug("Zip Path: " + zipPath);
            }
            if (zipPath.startsWith("yanelresource:") || contentRepo.getNode(zipPath).isResource()) {
                if (log.isDebugEnabled()) {
                    log.debug("Is Resource: " + zipPath);
                }
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ZipOutputStream zipOut = new ZipOutputStream(byteOut);
                try {
                    InputStream is = null;
                    String entryPath;
                    if (zipPath.startsWith("yanelresource:")) {
                        SourceResolver resolver = new SourceResolver(this);
                        Source source = resolver.resolve(zipPath, null);
                        is = ((StreamSource) source).getInputStream();
                        entryPath = PathUtil.getName(zipPath);
                    } else {
                        is = contentRepo.getNode(zipPath).getInputStream();
                        entryPath = contentRepo.getNode(zipPath).getName();
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("Zip entry path: " + entryPath);
                    }
                    zipOut.putNextEntry(new ZipEntry(entryPath));
                    int rb = 0;
                    byte[] buf = new byte[8192];
                    while ((rb = is.read(buf)) > 0) {
                        zipOut.write(buf, 0, rb);
                    }
                    zipOut.closeEntry();
                    is.close();
                } catch (Exception e) {
                    log.error(e);
                }
                zipOut.close();
                ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
                defaultView.setInputStream(byteIn);
                defaultView.setMimeType("application/zip");
            } else if (contentRepo.getNode(zipPath).isCollection()) {
                if (log.isDebugEnabled()) {
                    log.debug("Is Collection: " + zipPath);
                }
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ZipOutputStream zipOut = new ZipOutputStream(byteOut);
                Node[] zipDirEntries = contentRepo.getNode(zipPath).getNodes();
                addZipEntries(zipOut, zipDirEntries, "");
                zipOut.close();
                ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
                defaultView.setInputStream(byteIn);
                defaultView.setMimeType("application/zip");
            } else {
                log.warn("Neither resource nor collection");
            }
        } catch (Exception e) {
            log.error(e);
        }
        return defaultView;
    }

    /**
     * @param ZipOutputStream zipOut
     * @param Node[] entries
     * @param String base
     */
    protected void addZipEntries(ZipOutputStream zipOut, Node[] entries, String base) throws Exception {
        String entryPath = null;
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].isResource()) {
                int rb = 0;
                byte[] buf = new byte[8192];
                InputStream in = null;
                try {
                    entryPath = base + entries[i].getName();
                    if (log.isDebugEnabled()) {
                        log.debug("Zip entry path: " + entryPath);
                    }
                    zipOut.putNextEntry(new ZipEntry(entryPath));
                    in = entries[i].getInputStream();
                    while ((rb = in.read(buf)) > 0) {
                        zipOut.write(buf, 0, rb);
                    }
                    zipOut.closeEntry();
                    in.close();
                } catch (Exception e) {
                    log.error(e);
                }
            } else if (entries[i].isCollection() && !entries[i].getName().startsWith(".")) { // ignore dot files
                entryPath = base + entries[i].getName() + "/";
                if (log.isDebugEnabled()) {
                    log.debug("Zip dir path: " + entryPath);
                }
                // directory entries end with a "/"
                ZipEntry zipEntry = new ZipEntry(entryPath);
                try {
                    zipOut.putNextEntry(zipEntry);
                    zipOut.closeEntry();
                } catch (IOException e) {
                    log.error(e);
                }
                // recurse
                addZipEntries(zipOut, entries[i].getNodes(), entryPath);
            }
        }
    }

    public String getMimeType(String viewId) throws Exception {
        return "application/zip";
    }

    public boolean exists() throws Exception {
        log.warn("Not implemented yet!");
        return true;
    }

    public long getSize() throws Exception {
        // TODO: not implemented yet
        log.warn("TODO: Method is not implemented yet");
        return -1;
    }
}
