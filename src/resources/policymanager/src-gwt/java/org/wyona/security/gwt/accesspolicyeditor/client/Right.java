/*
 * Copyright 2008 Wyona
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.wyona.security.gwt.accesspolicyeditor.client;

/**
 *
 */
public class Right {

    private String id;
    private String label;
    private boolean permission;

    /**
     *
     */
    public Right(String id, boolean permission) {
        this.id = id;
        this.permission = permission;
    }

    /**
     * @param id ID of right, for example "r" or "open"
     * @param label Label of right, for example "Read" or "Open for editing"
     */
    public Right(String id, String label) {
        this.id = id;
        this.label = label;
        this.permission = false;
    }

    /**
     *
     */
    public String getId() {
        return id;
    }

    /**
     *
     */
    public String getLabel() {
        return label;
    }

    /**
     *
     */
    public boolean getPermission() {
        return permission;
    }
}
