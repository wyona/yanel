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
 * Cut and paste users and groups in order to change list order
 */
public class CutPasteListOrderWidget extends Composite implements ClickListener {

    private PolicyListBoxWidget policyLBW;

    private FlowPanel fp = new FlowPanel();

    private Button cutButton;
    private Button pasteAboveButton;
    private Button pasteBelowButton;

    private String selectedCutItem = null;
    private Right[] selectedCutRights = null;
    private String selectedPasteAboveItem = null;
    private String selectedPasteBelowItem = null;

    /**
     *
     */
    public CutPasteListOrderWidget(PolicyListBoxWidget policyLBW) {
        initWidget(fp);

        cutButton = new Button("Cut", this);
        fp.add(cutButton);

        pasteAboveButton = new Button("Paste above", this);
        fp.add(pasteAboveButton);

        pasteBelowButton = new Button("Paste below", this);
        fp.add(pasteBelowButton);

        this.policyLBW = policyLBW;
    }

    /**
     * Move item from one list to the other
     */
    public void onClick(Widget sender) {
        ListBox policyLB = policyLBW.getListBox();
        if (sender == cutButton) {
            //Window.alert("DEBUG: Cut user or group ...");
            boolean noItemSelected = true;

            for (int i = policyLB.getItemCount() - 1; i >= 0; i--) { // INFO: One needs to step backwards, because the size of the list decreases, because items are being removed if selected
                if (policyLB.isItemSelected(i)) {
                    selectedCutItem = policyLB.getValue(i);
                    //Window.alert("INFO: Item '" + selectedCutItem + "' has been selected to be moved within policy. Select now another item and click either the 'Paste above' or 'Paste below' button.");

                    //Window.alert("DEBUG: Selected item text: " + policyLB.getItemText(i));
                    selectedCutRights = policyLBW.getRights(policyLB.getItemText(i));

                    // TODO: Discuss of "Cut" only when pasted!
                    policyLB.removeItem(i);

                    noItemSelected = false;
                    break;
                }
            }

            if (noItemSelected) {
                Window.alert("No identity selected yet! Please select an identity from 'Policy' list in order to move up or down.");
            }
        } else if (sender == pasteAboveButton) {
            //Window.alert("DEBUG: Paste above user or group ...");
            boolean noItemSelected = true;

            if (selectedCutItem == null) {
                Window.alert("No cut identity selected yet! Please select an identity from 'Policy' list and click the 'Cut' button.");
                return;
            }

            for (int i = policyLB.getItemCount() - 1; i >= 0; i--) { // INFO: One needs to step backwards, because the size of the list decreases, because items are being removed if selected
                if (policyLB.isItemSelected(i)) {
                    selectedPasteAboveItem = policyLB.getValue(i);
                    if (selectedCutItem.equals(selectedPasteAboveItem)) {
                        Window.alert("WARN: The paste above identity selected is the same as the cut identity! Please select another identity from 'Policy' list and click the 'Paste above' button again.");
                        return;
                    }

                    //Window.alert("DEBUG: Paste the item '" + selectedCutItem + "' above the selected identity '" + selectedPasteAboveItem + "'.");
                    String type = selectedCutItem.substring(0, 1);
                    String name = selectedCutItem.substring(2).trim();
                    String typeInsertBefore = selectedPasteAboveItem.substring(0, 1);
                    String nameInsertBefore = selectedPasteAboveItem.substring(2).trim();
                    boolean keepItemSelected = true; // TODO: Discuss whether item should be selected (see e-mail ...)
                    policyLBW.insertItemBefore(type, name, selectedCutRights, keepItemSelected, typeInsertBefore, nameInsertBefore);

                    //Window.alert("DEBUG: Paste above is completed.");

                    // Reset temporary variables
                    selectedCutItem = null;
                    selectedCutRights = null;
                    selectedPasteAboveItem = null;
                    selectedPasteBelowItem = null;

                    noItemSelected = false;
                    break;
                }
            }

            if (noItemSelected) {
                Window.alert("No identity selected yet! Please select an identity from 'Policy' list in order to paste above.");
            }
        } else if (sender == pasteBelowButton) {
            boolean noItemSelected = true;

            if (selectedCutItem == null) {
                Window.alert("No cut identity selected yet! Please select an identity from 'Policy' list and click the 'Cut' button.");
                return;
            }

            for (int i = policyLB.getItemCount() - 1; i >= 0; i--) { // INFO: One needs to step backwards, because the size of the list decreases, because items are being removed if selected
                if (policyLB.isItemSelected(i)) {
                    selectedPasteBelowItem = policyLB.getValue(i);
                    if (selectedCutItem.equals(selectedPasteBelowItem)) {
                        Window.alert("WARN: The paste below identity selected is the same as the cut identity! Please select another identity from 'Policy' list and click the 'Paste below' button again.");
                        return;
                    }

                    //Window.alert("DEBUG: Paste the item '" + selectedCutItem + "' below the selected identity '" + selectedPasteBelowItem + "'.");

                    String type = selectedCutItem.substring(0, 1);
                    String name = selectedCutItem.substring(2).trim();
                    String typeInsertAfter = selectedPasteBelowItem.substring(0, 1);
                    String nameInsertAfter = selectedPasteBelowItem.substring(2).trim();
                    boolean keepItemSelected = true; // TODO: Discuss whether item should be selected (see e-mail ...)
                    policyLBW.insertItemAfter(type, name, selectedCutRights, keepItemSelected, typeInsertAfter, nameInsertAfter);

                    //Window.alert("DEBUG: Paste below is completed.");

                    // Reset temporary variables
                    selectedCutItem = null;
                    selectedCutRights = null;
                    selectedPasteAboveItem = null;
                    selectedPasteBelowItem = null;

                    noItemSelected = false;
                    break;
                }
            }

            if (noItemSelected) {
                Window.alert("No identity selected yet! Please select an identity from 'Policy' list in order to paste below.");
            }
        } else {
            Window.alert("Sender not implemented!");
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
