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

import org.wyona.yanel.core.attributes.viewable.View;

/**
 * DEV (not released yet, this interface still might change ...)
 */
public interface VersionableV2 {

    /**
     * Gets an array of revision names.
     * @return
     * @throws Exception
     */
    public String[] getRevisions() throws Exception;
    
    /**
     * Gets the view of a certain revision.
     * @param viewId
     * @param revisionName
     * @return
     * @throws Exception
     */
    public View getView(String viewId, String revisionName) throws Exception;
    
    /**
     * Restores an old revision with the given revision number.
     * @param revisionName
     * @throws Exception
     */
    public void restore(String revisionName) throws Exception;
    
    /**
     * Checks out this resource. Noone else will be able to check it out afterwards.
     * @param userID
     * @throws Exception
     */
    public void checkout(String userID) throws Exception;
    
    /**
     * Checks in this resource, and creates a new revision.
     * @throws Exception
     */
    public void checkin() throws Exception;
    
    
    /*
     * Methods which could be added to this interface:
     * 
     *
     * public boolean isCheckedOut() throws Exception;
     * public String getCheckoutUserID() throws Exception;
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
