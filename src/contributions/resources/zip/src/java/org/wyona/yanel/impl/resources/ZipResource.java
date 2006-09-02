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

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Category;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yarep.util.RepoPath;

/**
 *
 */
public class ZipResource extends Resource implements ViewableV1 {

    private static Category log = Category.getInstance(ZipResource.class);

    /**
     *
     */
    public ZipResource() {
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
            String zipDir = getProperty(path, "zip-dir");
            log.error("DEBUG: Zip Directory: " + zipDir);
            org.wyona.yarep.core.Path zipDirPath = new org.wyona.yarep.core.Path(zipDir); 
            
            RepoPath zipRp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(
                    new org.wyona.yarep.core.Path(path.toString()), new RepositoryFactory());
            
            Repository zipRepo = zipRp.getRepo();         
            
            log.error("DEBUG: exists: " + zipRepo.exists(zipDirPath));
            
            if (zipRepo.isCollection(zipDirPath)) {
                log.error("DEBUG: Collection: " + zipDirPath);                
                                                
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ZipOutputStream zipOut = new ZipOutputStream(byteOut);                        
                
                org.wyona.yarep.core.Path[] zipDirEntries = zipRepo.getChildren(zipDirPath);
                
                addZipEntries(zipOut, zipRepo, zipDirEntries, "", zipDirPath);
                
                zipOut.close();
                
                ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
                defaultView.setInputStream(byteIn);                    
                defaultView.setMimeType("application/zip");
            } else if (zipRepo.isResource(zipDirPath)){
                log.error("DEBUG: Resource: " + zipDir);
            } else {
                log.error("DEBUG: neither resource nor collection");
            }
            
        } catch(Exception e) {
            log.error(e);
        }
        
        return defaultView;
    }

    /**
     * @param zipOut
     * @param repo
     * @param entries
     */
    void addZipEntries(ZipOutputStream zipOut, Repository repo, org.wyona.yarep.core.Path[] entries, String base, org.wyona.yarep.core.Path zipDirPath) {
        
        String entryPath = null;
        
        for (int i=0; i<entries.length; i++) {            
            
            if (repo.isResource(entries[i])) {                
                int rb = 0;
                byte[] buf = new byte[8192];                    
                InputStream in = null;
                
                try {
                    entryPath = base + entries[i].getName();
                    
                    log.error("DEBUG: Zip entry path: " + entryPath);                    
                    
                    zipOut.putNextEntry(new ZipEntry(entryPath));
                    // FIXME how to get an inpustream from a collection entry?
                    org.wyona.yarep.core.Path yarepPath = new org.wyona.yarep.core.Path(zipDirPath.toString() + "/" + entryPath);                    
                    log.error("DEBUG: Yarep path: " + yarepPath);
                    in = repo.getInputStream(yarepPath);                    
                    while ((rb = in.read(buf)) > 0) {
                        zipOut.write(buf, 0, rb);
                    }                    
                    zipOut.closeEntry();
                    in.close();                    
                } catch (Exception e) {
                    log.error(e);
                }                
            } else if (repo.isCollection(entries[i]) && 
                    !entries[i].getName().startsWith(".")) { // ignore dot files
                
                entryPath = base + entries[i].getName() + "/";
                
                log.error("DEBUG: Zip dir path: " + entryPath);                
                
                // directory entries end with a "/"
                ZipEntry zipEntry = new ZipEntry(entryPath);
                try {
                    zipOut.putNextEntry(zipEntry);
                    zipOut.closeEntry();
                } catch (IOException e) {
                    log.error(e);
                }
                
                // recurse
                addZipEntries(zipOut, repo, repo.getChildren(entries[i]), entryPath, zipDirPath);
            }
        }    
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
    private String getProperty(Path path, String name) {
        String property = null;
        try {
            // TODO: Get yanel RTI yarep properties file name from framework resp. use MapFactory ...!
            RepoPath rpRTI = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), new RepositoryFactory("yanel-rti-yarep.properties"));
            java.io.BufferedReader br = new java.io.BufferedReader(rpRTI.getRepo().getReader(new org.wyona.yarep.core.Path(new Path(rpRTI.getPath().toString()).getRTIPath().toString())));

            while ((property = br.readLine()) != null) {
                if (property.indexOf(name + ":") == 0) {
                    property = property.substring(name.length() + 2);
                    log.info("*" + property + "*");
                    return property;
                }
            }
        } catch(Exception e) {
            log.warn(e);
        }

        return property;
    }
}
