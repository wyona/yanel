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

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.attributes.View;
import org.wyona.yanel.core.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.ViewDescriptor;

import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;

/**
 *
 */
public class FileResource extends Resource implements ViewableV1 {

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
    public View getView(String suffix, String viewId) {
        View defaultView = new View();
        defaultView.setMimeType("application/octet-stream");
        try {
            Repository repo = new RepositoryFactory().newRepository("yanel");
            defaultView.setInputStream(repo.getInputStream(new org.wyona.yarep.core.Path("/hello/world.html")));
            //defaultView.setInputStream(new java.io.FileInputStream("/home/USER/content/test.txt"));
        } catch(Exception e) {
            System.err.println(e);
        }
        return defaultView;
    }
}
