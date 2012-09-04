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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

/**
 * List box containing users and groups to choose/select from
 */
public class IdentitiesListBoxWidget extends Composite implements ClickListener {

    private ListBox lb;

    private VerticalPanel vp = new VerticalPanel();

    private String language;

    private List users;
    private List groups;

    /**
     * @param visibleItemCount Maximum number of users/groups which shall be displayed (all other users/groups can be accessed by scrolling up and down)
     * @param language I18n language
     */
    public IdentitiesListBoxWidget(int visibleItemCount, String language) {
        this.language = language;
        initWidget(vp);

        vp.add(new Label(I18n.getLabel("list-box-identities", language) + ":"));

        boolean isMultipleSelect = true;
        lb = new ListBox(isMultipleSelect); // NOTE: ListBox#setMultipleSelect(true) can spuriously fail on IE 6.0
        lb.addClickListener(this);

        lb.clear();
        lb.setVisibleItemCount(visibleItemCount);
        displayLoadingMessage();

        vp.add(lb);
    }

    /**
     * Display message that users and groups have not been loaded yet
     */
    public void displayLoadingMessage() {
        lb.clear();
        lb.addItem("Loading users/groups ...");
        lb.addItem("Thanks for being patient!");
    }

    /**
     * Set users and groups as list items
     */
    public void setUsersAndGroups(String[] users, String[] groups) {
        if (users == null && groups == null) {
            Window.alert("ERROR: Neither users nor groups set!");
            return;
        }

        if (users != null) {
            this.users = new java.util.ArrayList();
            for (int i = 0; i < users.length; i++) {
                this.users.add(users[i]);
            }
        }
        if (groups != null) {
            this.groups = new java.util.ArrayList();
            for (int i = 0; i < groups.length; i++) {
                this.groups.add(groups[i]);
            }
        }

        listAll();
    }

    /**
     * Display all users and all groups
     */
    public void listAll() {
        if (users == null && groups == null) {
            Window.alert("ERROR: Neither users nor groups have been set yet!");
            return;
        }

        lb.clear();
        if (users != null) {
            for (int i = 0; i < users.size(); i++) {
                lb.addItem("u: " + users.get(i));
            }
        } else {
            lb.addItem("No users yet!");
        }
        if (groups != null) {
            for (int i = 0; i < groups.size(); i++) {
                lb.addItem("g: " + groups.get(i));
            }
        } else {
            lb.addItem("No groups yet!");
        }
    }

    /**
     *
     */
    public ListBox getListBox() {
        return lb;
    }

    /**
     *
     */
    public void onClick(Widget sender) {
        //Window.alert("An identity has been selected!");
    }

    /**
     * List all users and groups matching a particular search term
     * @param searchTerm Search term
     */
    public void listBySearchMatch(String searchTerm) {
        lb.clear();

        for (int i = 0; i < users.size(); i++) {
            String itemText = (String) users.get(i);
            if (itemText.indexOf(searchTerm) >= 0) {
                lb.addItem("u: " + itemText);
            }
        }
        for (int i = 0; i < groups.size(); i++) {
            String itemText = (String) groups.get(i);
            if (itemText.indexOf(searchTerm) >= 0) {
                lb.addItem("g: " + itemText);
            }
        }
    }

    /**
     * Remove user
     * @param id User ID
     */
    public void removeUser(String id) {
        //Window.alert("DEBUG: Remove user: " + id);
        users.remove(id);
    }

    /**
     * Add user
     * @param id User ID
     */
    public void addUser(String id) {
        //Window.alert("DEBUG: Add user: " + id);
        users.add(id);
    }

    /**
     * Remove group
     * @param id Group ID
     */
    public void removeGroup(String id) {
        groups.remove(id);
    }

    /**
     * Add group
     * @param id Group ID
     */
    public void addGroup(String id) {
        groups.add(id);
    }

    /**
     * List users and groups within a specific range of the alphabet
     * @param start Start of alphabet range
     * @param end End of alphabet range
     */
    public void listUsersAndGroups(char start, char end) {
        //Window.alert("DEBUG: List users and groups ...");

        if (groups != null && users != null) {
            lb.clear();

            for (int i = 0; i < users.size(); i++) {
                char firstChar = ((String)users.get(i)).charAt(0);
                if (start <= firstChar && firstChar <= end) {
                    lb.addItem("u: " + users.get(i));
                }
            }

            for (int i = 0; i < groups.size(); i++) {
                char firstChar = ((String)groups.get(i)).charAt(0);
                if (start <= firstChar && firstChar <= end) {
                    lb.addItem("g: " + groups.get(i));
                }
            }

/*
            if (lb.getItemCount() <= 0) {
                lb.addItem("Neither users nor groups within range: " + start + " - " + end);
            }
*/
        } else {
            lb.addItem("Either users or groups not loaded yet!");
        }
    }
}
