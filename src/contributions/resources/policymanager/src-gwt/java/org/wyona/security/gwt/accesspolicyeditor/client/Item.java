/*
 * Copyright 2010 Wyona
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
public class Item {

    private String id;
    private Right[] rights;

    /**
     *
     */
    public Item(String id, Right[] rights) {
        this.id = id;
        this.rights = rights;
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
    public Right[] getRights() {
        return rights;
    }
}
