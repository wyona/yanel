/*
 * Copyright 2010 Wyona
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

import org.wyona.security.core.api.User;
import org.wyona.security.core.api.UserManager;
import org.wyona.security.core.api.AccessManagementException;
import org.wyona.yanel.impl.resources.usecase.UsecaseException;
import org.wyona.yanel.impl.resources.usecase.UsecaseResource;

import org.apache.log4j.Logger;

import java.lang.System;
import java.lang.Integer;
import java.lang.Boolean;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * Resource to list all users.
 */
public class ListUsersResource extends UsecaseResource {
    // Constants
    private static final int DEFAULT_ITEMS_PER_PAGE = 10;
    //private static final int DEFAULT_ITEMS_PER_PAGE = 100;
    private static final Logger log =  Logger.getLogger(ListUsersResource.class);

    // Variables
    private int currentPage = 1;
    private int totalPages = 1;
    private int itemsPerPage = 1;
    private int lowerBound = 1;
    private int upperBound = 1;
    private int totalUsers = 0;
    private boolean hasNext = false;
    private List<User> users = null;
    private boolean initialized = false;

    /**
     * Initialize all variables.
     * This function intializes various private variables. You don't need
     * to call this function, it will be called automatically the first time
     * you access a variable and the object finds that it is not initialized.
     */
    private void initVars() throws UsecaseException {
        UserManager userManager = getRealm().getIdentityManager().getUserManager();

        // Pagination
        // Current page
        try {
            String p = getParameterAsString("page");
            currentPage = Integer.parseInt(p);
            if(currentPage < 1) currentPage = 1;
        } catch(Exception e) {
            currentPage = 1;
        }

        // Items per page
        try {
            String i = getResourceConfigProperty("items-per-page");
            itemsPerPage = Integer.parseInt(i);
            if(itemsPerPage < 1) itemsPerPage = 1;
        } catch(Exception e) {
            itemsPerPage = DEFAULT_ITEMS_PER_PAGE;
        }

        // Result
        try {
            // All users matching search term,
            // or all users overall if search term is empty
            Iterator<User> allUsers;
            String query = getParameterAsString("query");

            if(query != null && !"".equals(query)) { 
                try {
                    // TODO: What if getUsers() returns garbage?
                    allUsers = userManager.getUsers(query);
                } catch(Exception e) {
                    log.warn(e, e);
                    lowerBound = 0;
                    upperBound = 0;
                    totalUsers = 0;
                    return;
                }
            } else {
                allUsers = userManager.getAllUsers();
            }

            // Boundaries...
            lowerBound = (currentPage-1)*itemsPerPage;
            upperBound = lowerBound+itemsPerPage-1; // TODO: On the very last page this doesn't have to be correct
            totalUsers = userManager.getUserCount();
            totalPages = totalUsers/itemsPerPage;
            if(totalUsers%itemsPerPage != 0) totalPages++;

            int idx = 0;
            users = new LinkedList<User>();
            while(allUsers.hasNext()) {
                User current = allUsers.next();
                if(idx >= lowerBound && idx <= upperBound) {
                    users.add(current);
                } else if(idx >= upperBound) {
                    break;
                }
                idx = idx + 1;
            }

            if(idx <= upperBound) {
                hasNext = false;
                upperBound = idx - 1;
            } else {
                hasNext = true;
            }

            lowerBound++;
            upperBound++;
            initialized = true;
        } catch (AccessManagementException e) {
            initialized = false;
            throw new UsecaseException(e.toString(), e);
        }
    }

    /**
     * Get users on current page.
     */
    public Iterator<User> getUsers() throws UsecaseException {
        if(!initialized) initVars();
        return users.iterator();
    }

    /**
     * Get the current page being displayed.
     */
    public String getCurrentPage() throws UsecaseException {
        if(!initialized) initVars();
        return Integer.toString(currentPage);
    }

    /**
     * Get the lower bound being displayed.
     * For example, if we're on page 2 and there are
     * 10 users being displayed per page, this value
     * will be 11 - because the first user on the page
     * is user number 11 in the array.
     */
    public String getLowerBound() throws UsecaseException {
        if(!initialized) initVars();
        return Integer.toString(lowerBound);
    }

    /**
     * Get the upper bound being displayed.
     * For example, if we're on page 2 and there are
     * 10 users being displayed per page, this value
     * will be 20 - because the last user on the page
     * is user number 20 in the array.
     */
    public String getUpperBound() throws UsecaseException {
        if(!initialized) initVars();
        return Integer.toString(upperBound);
    }

    /**
     * Get the total amount of pages.
     */
    public String getTotalPages() throws UsecaseException {
        if(!initialized) initVars();
        return Integer.toString(totalPages);
    }

    /**
     * Get the total amount of existing users.
     */
    public String getTotalUsers() throws UsecaseException {
        if(!initialized) initVars();
        return Integer.toString(totalUsers);
    }

    /**
     * Check if there is another page.
     * Return true if there is another page beyond the 
     * current one, false if there is not.
     */
    public String hasNext() throws UsecaseException {
        if(!initialized) initVars();
        return Boolean.toString(hasNext);
    }
}
