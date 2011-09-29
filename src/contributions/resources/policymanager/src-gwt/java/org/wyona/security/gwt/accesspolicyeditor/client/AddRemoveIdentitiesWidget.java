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
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Move users/groups from identities list into policy and vice versa
 */
public class AddRemoveIdentitiesWidget extends Composite implements ClickListener {

    private IdentitiesListBoxWidget identitiesLBW;
    private ListBox policyLB;
    private PolicyListBoxWidget policyLBW;

    private FlowPanel fp = new FlowPanel();

    private Button addButton;
    private Button removeButton;

    /**
     *
     */
    public AddRemoveIdentitiesWidget(IdentitiesListBoxWidget identitiesListBox, ListBox policyListBox, PolicyListBoxWidget policyLBW) {
        initWidget(fp);

        addButton = new Button(">", this);
        fp.add(addButton);

        removeButton = new Button("<", this);
        fp.add(removeButton);

        this.identitiesLBW = identitiesListBox;
        this.policyLB = policyListBox;
        this.policyLBW = policyLBW;
    }

    /**
     * Move item from one list to the other
     */
    public void onClick(Widget sender) {
        ListBox identitiesLB = identitiesLBW.getListBox();
        if (sender == addButton) {
            boolean noItemSelected = true;
            for (int i = identitiesLB.getItemCount() - 1; i >= 0; i--) { // INFO: One needs to step backwards, because the size of the list decreases, because items are being removed if selected
                if (identitiesLB.isItemSelected(i)) {
                    String selectedIdentity = identitiesLB.getValue(i);
                    //Window.alert("DEBUG: Move item: " + selectedIdentity);
                    if (selectedIdentity.startsWith("u:")) {
                        identitiesLBW.removeUser(selectedIdentity.substring(3));
                    } else if (selectedIdentity.startsWith("g:")) {
                        identitiesLBW.removeGroup(selectedIdentity.substring(3));
                    } else {
                        Window.alert("ERROR: Neither user nor group: " + selectedIdentity);
                    }
                    identitiesLB.removeItem(i);
                    String type = selectedIdentity.substring(0, 1); // e.g. 'g' or 'u'
                    String name = selectedIdentity.substring(2).trim(); // e.g. 'lenya'
                    //Window.alert("Add selected identity " + selectedIdentity + " (" + item + ", " + value + ") to policy");
                    policyLBW.insertItemAtTop(type, name, true);
                    noItemSelected = false;
                }
            }

            if (noItemSelected) {
                Window.alert("No identity selected yet! Please select an identity within 'Identities' list.");
            }
        } else if (sender == removeButton) {
            boolean noItemSelected = true;
            for (int i = policyLB.getItemCount() - 1; i >= 0; i--) { // INFO: One needs to step backwards, because the size of the list decreases, because items are being removed if selected
                if (policyLB.isItemSelected(i)) {
                    String selectedIdentity = policyLB.getValue(i);
                    //Window.alert("DEBUG: Remove selected identity " + selectedIdentity + " from policy");
                    if (selectedIdentity.startsWith("u:")) {
                        identitiesLBW.addUser(selectedIdentity.substring(3));
                    } else if (selectedIdentity.startsWith("g:")) {
                        identitiesLBW.addGroup(selectedIdentity.substring(3));
                    } else {
                        Window.alert("ERROR: Neither user nor group: " + selectedIdentity);
                    }
                    policyLB.removeItem(i);
                    identitiesLB.addItem(removeRights(selectedIdentity));
                    noItemSelected = false;
                }
            }

            if (noItemSelected) {
                Window.alert("No identity selected yet! Please select an identity within 'Policy' list.");
            }
        }
    }

    /**
     * Remove rights from string, e.g. "U: alice (Read, Write)" -> "U: alice"
     */
    private String removeRights(String identity) {
        if (identity.indexOf("(") > 0) {
            return identity.substring(0, identity.indexOf("("));
        } else {
            return identity;
        }
    }
}
