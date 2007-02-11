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

import java.util.Date;

import org.wyona.yanel.core.attributes.versionable.RevisionInformation;
import org.wyona.yanel.core.attributes.viewable.View;

/**
 * DEV (not released yet, this interface still might change ...)
 */
public interface VersionableV2 {

    /**
     * Gets an array of revision information objects.
     * @return informations of the various revisions
     * @throws Exception
     */
    public RevisionInformation[] getRevisions() throws Exception;
    
    /**
     * Gets the view of a certain revision.
     * @param viewId
     * @param revisionName
     * @return view of resource
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
     * Puts this resource into checked-out state. Noone else will be able to check it out afterwards.
     * @param userID
     * @throws Exception
     */
    public void checkout(String userID) throws Exception;
    
    /**
     * Puts this resource into checked-in state, and creates a new revision.
     * @throws Exception
     */
    public void checkin(String comment) throws Exception;
    
    
    /**
     * Indicates whether this resource is checked out.
     * @return boolean to indicate of resource is checked out nor not
     * @throws Exception
     */
    public boolean isCheckedOut() throws Exception;
    
    /**
     * Returns the user id which was supplied when calling checkout()
     * @return user id who checked out resource
     * @throws Exception
     */
    public String getCheckoutUserID() throws Exception;
    
    /**
     * Returns the date when this resource was checked out.
     * @return date
     * @throws Exception
     */
    public Date getCheckoutDate() throws Exception;
    
    /*
     * Methods which could be added to this interface:
     * 
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
