/*
 * Copyright 2011 Wyona
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

import java.util.Date;

import org.wyona.yanel.core.attributes.versionable.RevisionInformation;
import org.wyona.yanel.core.attributes.viewable.View;

/**
 * DEV (not released yet, this interface still might change ...)
 */
public interface VersionableV3 {

    /**
     * Gets an iterator of revision information objects (newest/head revision first, oldest at the last position)
     * @param reverse Reverse order by date. If set to true, then the oldest revision is first and the newest/head is last.
     * @return information of the various revisions, or empty iterator if there are no revisions.
     * @throws Exception
     */
    public java.util.Iterator<RevisionInformation> getRevisions(boolean reverse) throws Exception;

    /**
     * Puts this resource into checked-in state, and creates a new revision.
     * @throws Exception
     */
/*
    public RevisionInformation checkin(String comment) throws Exception;
*/

    /**
     * Gets an iterator of revision information objects (newest/head revision first, oldest at the last position)
     * @return information of the various revisions, or empty iterator if there are no revisions.
     * @throws Exception
     */
/*
    public TopRevisions getRevisions(String query, int n) throws Exception;
*/
    
    /*
     * TODO: Methods which could be added to a new version of this interface:
     *
     * getDiff(String rev1, String rev2) throws Exception;
     * getHeadRevisionNumber() ?
     * 
     * open questions:
     * - how to tag a revision? (user can tag a revision with a message like e.g. 'added paragraph about ...')
     * - how to retrieve information associated to a certain revision (date, tag, etc.)?
     * - how to integrate versioning with workflow: tag specific revision as live, etc.
     * 
     */
}
