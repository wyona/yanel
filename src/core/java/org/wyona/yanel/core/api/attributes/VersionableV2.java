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

package org.wyona.yanel.core.api.attributes;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.attributes.viewable.View;

/**
 * DEV (not released yet, this interface still might change ...)
 */
public interface VersionableV2 {

    public String[] getRevisions() throws Exception;
    
    /*
     * Methods which could be added to this interface:
     * 
     * public View getView(Path path, OutputStream out, String viewId, String revision) throws Exception;
     * public void getView(HttpServletRequest request, HttpServletResponse response, String viewId, String revision) throws Exception;
     * public void rollback(String revision) throws Exception;
     *
     * getDiff(Path path, String rev1, String rev2) throws Exception;
     * getHeadRevisionNumber() ?
     * 
     * open questions:
     * - how to tag a revision? (user can tag a revision with a message like e.g. 'added paragraph about ...')
     * - how to retrieve information associated to a certain revision (date, tag, etc.)?
     * - how to integrate versioning with workflow: tag specific revision as live, etc.
     * 
     */
    
}
