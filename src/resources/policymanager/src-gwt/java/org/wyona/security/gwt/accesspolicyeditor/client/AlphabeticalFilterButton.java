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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * Alphabetical filter button
 */
public class AlphabeticalFilterButton extends Button {

    /**
     *
     */
    public AlphabeticalFilterButton(char startRangeChar, char endRangeChar, IdentitiesListBoxWidget usersGroupsListBox) {
        // Init button
        super(Character.toUpperCase(startRangeChar) + "-" + Character.toUpperCase(endRangeChar));

        // Add click listener
        addClickListener(new UsersGroupsButtonClickListener(startRangeChar, endRangeChar, usersGroupsListBox));

        // Set tool tip
        setTitle(Character.toUpperCase(startRangeChar) + " - " + Character.toUpperCase(endRangeChar) + " filter button");
    }
}

/**
 *
 */
class UsersGroupsButtonClickListener implements ClickListener {

    private IdentitiesListBoxWidget ugl;
    private char startChar;
    private char endChar;

    /**
     *
     */
    public UsersGroupsButtonClickListener(char startRangeChar, char endRangeChar, IdentitiesListBoxWidget usersGroupsListBox) {
        this.ugl = usersGroupsListBox;
        this.startChar = startRangeChar;
        this.endChar = endRangeChar;
    }

    /**
     *
     */
    public void onClick(Widget sender) {
        //Window.alert("DEBUG: Button clicked ...");
        ugl.listUsersAndGroups(startChar, endChar);
    }
}
