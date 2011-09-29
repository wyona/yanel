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

import java.util.Vector;

/**
 *
 */
public class PolicyListBoxWidget extends Composite implements ClickListener {

    private ListBox lb;
    private CheckBox policyInheritanceCB;

    private VerticalPanel vp = new VerticalPanel();

    private Right[] availableRights;
    private CheckBox[] availableRightsCB;


    /**
     *
     */
    public PolicyListBoxWidget(int visibleItemCount, User[] users, Group[] groups, boolean useInheritedPolicies, String language) {
        initWidget(vp);

        vp.add(new Label("Policy:"));

        policyInheritanceCB = new CheckBox(I18n.getLabel("inherit-rights-label", language));
        setInheritRightsFlag(useInheritedPolicies);
        vp.add(policyInheritanceCB);

        lb = new ListBox(true); // NOTE: ListBox#setMultipleSelect(true) can spuriously fail on IE 6.0
        lb.addClickListener(this);
        //setIdentities(visibleItemCount, users, groups);
        displayLoading(visibleItemCount);
        vp.add(lb);

        set(null);
    }

    /**
     *
     */
    public void displayLoading(int visibleItemCount) {
        lb.clear();
        lb.setVisibleItemCount(visibleItemCount);
        lb.addItem("Loading policy ...");
        lb.addItem("Thanks for being patient!");
    }

    /**
     *
     */
    public void setIdentities(int visibleItemCount, User[] users, Group[] groups) {
        lb.clear();
        lb.setVisibleItemCount(visibleItemCount);
        if (users != null || groups != null) {
            if (users != null) {
                for (int i = 0; i < users.length; i++) {
                    String type = "u";
                    String id = users[i].getId();
                    Right[] rights = users[i].getRights();
                    //Window.alert("User: " + users[i].getId() + " (Number of rights: " + rights.length + ")");
                    lb.addItem(getListItemLabel(type, id, rights), getListItemValue(type, id));
                }
            }
            if (groups != null) {
                for (int i = 0; i < groups.length; i++) {
                    String type = "g";
                    String id = groups[i].getId();
                    Right[] rights = groups[i].getRights();
                    //Window.alert("Group: " + groups[i].getId() + " (Number of rights: " + rights.length + ")");
                    lb.addItem(getListItemLabel(type, id, rights), getListItemValue(type, id));
                }
            } else {
                Window.alert("No groups!");
            }
        } else {
            lb.addItem("No identities yet!");
        }
    }

    /**
     *
     */
    public void setInheritRightsFlag(boolean useInheritedPolicies) {
        //Window.alert("Set inherit rights checkbox: " + useInheritedPolicies);
        if (policyInheritanceCB != null) {
            policyInheritanceCB.setChecked(useInheritedPolicies);
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
        CheckBox selectedRightCB = null;
        Right selectedRight = null;
        for (int i = 0; i < availableRightsCB.length; i++) {
            if (sender == availableRightsCB[i]) {
                selectedRightCB = availableRightsCB[i];
                selectedRight = availableRights[i];
                break;
            }
        }
        if (selectedRightCB != null) {

            String selectedIdentity = getSelectedItemText();
            //Window.alert("Right checkbox has been selected (selected identity: " + selectedIdentity + ")!");
            if (selectedIdentity != null) {
                Right[] currentRights = getRights(selectedIdentity);
                String[] newRights;
                if (selectedRightCB.isChecked()) {
                    //Window.alert("Add \"" + selectedRightCB.getName() + " (" + selectedRightCB.getText() + ")\" right of selected identity " + selectedIdentity + " to policy");
                    newRights = addRight(currentRights, selectedRight);
                } else {
                    //Window.alert("Remove \"" + selectedRightCB.getName() + " (" + selectedRightCB.getText() + ")\" right of selected identity " + selectedIdentity + " from policy");
                    newRights = removeRight(currentRights, selectedRight);
                }
                setRightsForSelectedListItem(newRights);
            } else {
                Window.alert("No identity has been selected! Please select an identity in order to assign rights.");
                selectedRightCB.setChecked(false);
            }
        } else if (sender == lb) {
            String selectedIdentity = getSelectedItemText();

            //Window.alert("Update check boxes!");
            Right[] rights = getRights(selectedIdentity);
            //Window.alert("Update checkboxes: " + rights.length + ", " + availableRightsCB.length);
            for (int j = 0; j < availableRightsCB.length; j++) {
                if (rights[j].getPermission()) {
                    availableRightsCB[j].setChecked(true);
                } else {
                    availableRightsCB[j].setChecked(false);
                }
            }
        }
    }

    /**
     * Get rights from identity string
     *
     * @param identity Item label containing type, rights and name, e.g. 'u:() alice'
     */
    Right[] getRights(String identity) {
        if (identity.indexOf("(") > 0) {
            String[] rightsString = identity.substring(identity.indexOf("(") + 1, identity.indexOf(")")).split(",");

            if (rightsString.length != availableRights.length) {
                Window.alert("Exception: Validation of rights length failed!");
                return null;
            }
            Right[] rights = new Right[availableRights.length];
            for (int i = 0; i < rightsString.length; i++) {
                if (rightsString[i].equals("-")) {
                    rights[i] = new Right(availableRights[i].getId(), false);
                } else {
                    rights[i] = new Right(rightsString[i], true);
                }
            }
            return rights;
        } else {
            // TODO: Return all rights with permission false
            return new Right[0];
        }
    }

    /**
     * @return Identity without rights, e.g. "u: alice" or "g: editors"
     */
    private String getIdentityWithoutRights(int index) {
        //Window.alert(getSelectedItemValue() + " --- " + getSelectedItemText());
        return lb.getValue(index);
    }

    /**
     *
     */
    private String getSelectedItemValue() {
        int i = lb.getSelectedIndex();
        if (i >= 0) {
            return lb.getValue(i);
        }
        return null;
    }

    /**
     *
     */
    private String getSelectedItemText() {
        int i = lb.getSelectedIndex();
        if (i >= 0) {
            return lb.getItemText(i);
        }
        return null;
    }

    /**
     *
     */
    private String[] addRight(Right[] currentRights, Right right) {
        //Window.alert("addRight(): Number of current rights: " + currentRights.length);
        //Window.alert("addRight(): Number of available rights: " + availableRights.length);

        String[] nRights = new String[availableRights.length];
        for (int i = 0; i < nRights.length; i++) {
            if (availableRights[i].getId().equals(right.getId())) {
                nRights[i] = right.getId();
            } else {
                if (currentRights[i].getPermission()) {
                    nRights[i] = currentRights[i].getId();
                } else {
                    nRights[i] = "-";
                }
            }
        }
        return nRights;
    }

    /**
     *
     */
    private String[] removeRight(Right[] currentRights, Right right) {
        //Window.alert("removeRight(): Number of current rights: " + currentRights.length);
        //Window.alert("removeRight(): Number of available rights: " + availableRights.length);

        String[] nRights = new String[availableRights.length];
        for (int i = 0; i < nRights.length; i++) {
            if (availableRights[i].getId().equals(right.getId())) {
                nRights[i] = "-";
            } else {
                if (currentRights[i].getPermission()) {
                    nRights[i] = currentRights[i].getId();
                } else {
                    nRights[i] = "-";
                }
            }
        }
        return nRights;
    }

    /**
     * Set rights for selected users/groups
     * 
     * @param rights New rights
     */
    private void setRightsForSelectedListItem(String[] rights) {
        boolean noItemSelected = true;

        for (int i = 0; i < lb.getItemCount(); i++) {
            if (lb.isItemSelected(i)) {
                String id = getIdentityWithoutRights(i);
                setListItem(id.substring(0, 1), id.substring(2).trim(), rights, i);
                noItemSelected = false;
            }
        }

        if (noItemSelected) {
            Window.alert("No user/group within 'Policy' list selected!");
        }
    }

    /**
     * @param type u for user and g for group
     * @param id
     * @param rights Rights
     * @param index Position of list item
     */
    private void setListItem(String type, String id, String[] rights, int index) {
        lb.setItemText(index, getListLabel(type, id, rights));
    }

    /**
     * @param type u for user and g for group
     * @param id
     * @param rights Rights
     */
    private String getListLabel(String type, String id, String[] rights) {
        StringBuffer sb = new StringBuffer(type + ":");

        sb.append("(" + rights[0]);
        for (int i = 1; i < rights.length; i ++) {
            sb.append("," + rights[i]);
        }
        sb.append(")");
        sb.append(" " + id);
        return sb.toString();
    }

    /**
     * Get list label, e.g. 'u:(r,-) alice'
     *
     * @param type u for user and g for group
     * @param id
     * @param rights Rights
     */
    private String getListLabel(String type, String id, Right[] rights) {
        StringBuffer sb = new StringBuffer(type + ":");

        if (rights[0].getPermission()) {
            sb.append("(" + rights[0].getId());
        } else {
            sb.append("(" + "-");
        }
        for (int i = 1; i < rights.length; i ++) {
            if (rights[i].getPermission()) {
                sb.append("," + rights[i].getId());
            } else {
                sb.append("," + "-");
            }
        }
        sb.append(")");
        sb.append(" " + id);
        return sb.toString();
    }


    /**
     * Generate list item value, e.g. "u: user-foo-bar"
     *
     * @param type u for user and g for group
     * @param id
     */
    private String getListItemValue(String type, String id) {
        return type + ": " + id;
    }

    /**
     * Generate list item label, e.g. "u:(view, -, toolbar) user-foo-bar"
     *
     * @param type u for user and g for group
     * @param id
     * @param rights Rights
     */
    private String getListItemLabel(String type, String id, Right[] rights) {

/*
        while (availableRights == null) {
            Window.alert("INFO: Available rights not loaded yet! Wait some more seconds ...");
            //sleep(1);
        }
*/

        StringBuffer sb = new StringBuffer(type + ":");

        if (availableRights != null) {
            sb.append("(");
            for (int i = 0; i < availableRights.length; i ++) {
                boolean rightExists = false;
                for (int k = 0; k < rights.length; k ++) {
                    if (availableRights[i].getId().equals(rights[k].getId()) && rights[k].getPermission()) {
                        rightExists = true;
                        break;
                    }
                }
                if (i > 0) {
                    sb.append(",");
                }
                if (rightExists) {
                    sb.append(availableRights[i].getId());
                } else {
                    sb.append("-");
                }
            }
            sb.append(")");
        } else {
            Window.alert("ERROR: Available rights are null (probably not loaded yet) and hence label cannot be completed!");
        }
        sb.append(" " + id);
        return sb.toString();
    }

    /**
     * Get all users and groups from list (including the rights)
     */
    public java.util.List getItems() {
        java.util.List items = new java.util.ArrayList();
        for (int i = 0; i < lb.getItemCount(); i++) {
            String itemText = lb.getItemText(i);
            Right[] rights = getRights(itemText);
            String id = getIdentityWithoutRights(i);
            if (id.startsWith("u:")) {
                items.add(new User(id.substring(2).trim(), rights));
            } else if (id.startsWith("g:")) {
                items.add(new Group(id.substring(2).trim(), rights));
            } else {
                Window.alert("ERROR: Neither user nor group: " + id);
            }
        }
        return items;
    }

    /**
     * Get all users from list (including the rights)
     */
    public User[] getUsers() {
        Vector users = new Vector();
        for (int i = 0; i < lb.getItemCount(); i++) {
            String itemText = lb.getItemText(i);
            Right[] rights = getRights(itemText);
            String id = getIdentityWithoutRights(i);
            if (id.startsWith("u:")) {
                users.add(new User(id.substring(2).trim(), rights));
            }
        }

        User[] u = new User[users.size()];
        for (int i = 0; i < u.length; i++) {
            u[i] = (User) users.elementAt(i);
        }
        return u;
    }

    /**
     * Get all users from list (including the rights)
     */
    public Group[] getGroups() {
        Vector groups = new Vector();
        for (int i = 0; i < lb.getItemCount(); i++) {
            String itemText = lb.getItemText(i);
            Right[] rights = getRights(itemText);
            String id = getIdentityWithoutRights(i);
            if (id.startsWith("g:")) {
                groups.add(new Group(id.substring(2).trim(), rights));
            }
        }

        Group[] g = new Group[groups.size()];
        for (int i = 0; i < g.length; i++) {
            g[i] = (Group) groups.elementAt(i);
        }
        return g;
    }

    /**
     *
     */
    public boolean getUseInheritedPolicies() {
        return policyInheritanceCB.isChecked();
    }

    /**
     * Set available rights (checkboxes)
     */
    public void set(Right[] availableRights) {
        this.availableRights = availableRights;
        if (availableRights != null) {
            availableRightsCB = new CheckBox[availableRights.length];
            for (int i = 0; i < availableRightsCB.length; i++) {
                // TODO: Also set label
                availableRightsCB[i] = new CheckBox(availableRights[i].getLabel());
                availableRightsCB[i].setName(availableRights[i].getId());
                availableRightsCB[i].addClickListener(this);
                vp.add(availableRightsCB[i]);
            }
        } else {
            //Window.alert("Available rights not loaded yet! Please don't worry, they will arrive soon hopefully!");
        }
    }

    /**
     * Append either user or group to policy list
     *
     * @param type Type of user or group
     * @param name Name of user or group
     * @param selected Sets whether list item is selected. True to select the item.
     */
    public void addItem(String type, String name, boolean selected) {
        lb.addItem(type + ":" + getEmptyRights() + " " + name, getListItemValue(type, name));
        lb.setItemSelected(lb.getItemCount() - 1, selected);
    }

    /**
     * Insert either user or group at top of policy list
     *
     * @param type Type of user or group
     * @param name Name of user or group
     * @param selected Sets whether list item is selected. True to select the item.
     */
    public void insertItemAtTop(String type, String name, boolean selected) {
        // TOOD: If selected is true, then unselect all other items!
        lb.insertItem(type + ":" + getEmptyRights() + " " + name, getListItemValue(type, name), 0);
        lb.setItemSelected(0, selected);
    }

    /**
     * Insert either user or group to policy list before an existing list item
     *
     * @param type Type of user or group which will be inserted
     * @param name Name of user or group which will be inserted
     * @param rights Rights of user or group which will be inserted
     * @param selected Sets whether list item is selected. True to select the item.
     * @param typeInsertBefore Type of user or group which is referenced as the target to insert before
     * @param nameInsertBefore Name of user or group which is referenced as the target to insert before
     */
    public void insertItemBefore(String type, String name, Right[] rights, boolean selected, String typeInsertBefore, String nameInsertBefore) {
        //Window.alert("DEBUG: Insert before ...");

        boolean targetItemExists = false;
        for (int i = lb.getItemCount() -1; i >= 0; i--) {
            //Window.alert("DEBUG: Item value: " + lb.getValue(i));
            if (lb.getValue(i).equals(getListItemValue(typeInsertBefore, nameInsertBefore))) {
                targetItemExists = true;
                lb.insertItem(getListLabel(type, name, rights), getListItemValue(type, name), i);
                break;
            }
        }

        if (!targetItemExists) {
            Window.alert("ERROR: No such item: " + getListItemValue(typeInsertBefore, nameInsertBefore));
        }
    }

    /**
     * Insert either user or group to policy list after an existing list item
     *
     * @param type Type of user or group which will be inserted
     * @param name Name of user or group which will be inserted
     * @param rights Rights of user or group which will be inserted
     * @param selected Sets whether list item is selected. True to select the item.
     * @param typeInsertBefore Type of user or group which is referenced as the target to insert before
     * @param nameInsertBefore Name of user or group which is referenced as the target to insert before
     */
    public void insertItemAfter(String type, String name, Right[] rights, boolean selected, String typeInsertBefore, String nameInsertBefore) {
        //Window.alert("DEBUG: Insert before ...");

        boolean targetItemExists = false;
        for (int i = lb.getItemCount() -1; i >= 0; i--) {
            //Window.alert("DEBUG: Item value: " + lb.getValue(i));
            if (lb.getValue(i).equals(getListItemValue(typeInsertBefore, nameInsertBefore))) {
                targetItemExists = true;
                if (i + 1 < lb.getItemCount()) {
                    lb.insertItem(getListLabel(type, name, rights), getListItemValue(type, name), i + 1);
                } else {
                    lb.addItem(getListLabel(type, name, rights), getListItemValue(type, name));
                }
                break;
            }
        }

        if (!targetItemExists) {
            Window.alert("ERROR: No such item: " + getListItemValue(typeInsertBefore, nameInsertBefore));
        }
    }

    /**
     * Generate an empty rights string
     */
    private String getEmptyRights() {
        StringBuffer emptyRights = new StringBuffer("(-");
        for (int i = 1; i < availableRightsCB.length; i++) {
            emptyRights.append(",-");
        }
        emptyRights.append(")");
        return emptyRights.toString();
    }
}
