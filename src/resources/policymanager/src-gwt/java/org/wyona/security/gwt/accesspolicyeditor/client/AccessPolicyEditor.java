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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.wyona.security.gwt.accesspolicyeditor.client.AddRemoveIdentitiesWidget;
import org.wyona.yanel.gwt.client.AsynchronousAgent;

/**
 * Access Policy Editor
 */
public class AccessPolicyEditor implements EntryPoint {

    User[] policyUsers;
    Group[] policyGroups;
    boolean useInheritedPolicies = true;

    IdentitiesListBoxWidget identitiesLBW;
    PolicyListBoxWidget policyLBW;
    Button saveButton;

    Right[] allRights;
    boolean rightsIdentitiesRetrievalCompleted = false;
    boolean policyRetrievalCompleted = false;

    // How many items shall be displayed within a list box, before one has to use the scroll-bar
    int visibleItemCount = 20;

    /**
     * Execute at the very beginning
     */
    public void onModuleLoad() {
        String language = "en"; // DEFAULT

        String identitiesURL = "DEFAULT-identities-and-usecases.xml";
        String readPolicyURL = "DEFAULT-policy.xml";
        boolean cancelURLBaseEqualsHostPageURL = false;
        String cancelURL = "DEFAULT-cancel.html";
        String savePolicyURL = "DEFAULT-save-policy.xml";
        String i18nURL = "DEFAULT-i18n.xml";
        // Get URLs from host/html page
        try {
            Dictionary dict = Dictionary.getDictionary("getURLs");
            language = dict.get("language");
            identitiesURL = dict.get("identities-url");
            readPolicyURL = dict.get("policy-url");
            cancelURL = dict.get("cancel-url").replace("&amp;", "&");
            cancelURLBaseEqualsHostPageURL = new Boolean(dict.get("cancel-url-base-equals-host-page-url")).booleanValue();
            savePolicyURL = dict.get("save-url");

            // TODO: Read this URL and use data instead from hardcoded I18n class
            i18nURL = dict.get("i18n-url");
        } catch (java.util.MissingResourceException e) {
            Window.alert("Exception: " + e.getMessage());
        }

        // Get identities/groups and available rights from server
        identitiesLBW = new IdentitiesListBoxWidget(visibleItemCount, language);
        getIdentitiesAndRights(identitiesURL);
        
        // Get policy from server (within getPolicy() the identities list will be updated accordingly)
        policyLBW = new PolicyListBoxWidget(visibleItemCount, policyUsers, policyGroups, useInheritedPolicies, language);
        getPolicy(readPolicyURL);

        // Setup GUI
        VerticalPanel vp = new VerticalPanel();
        vp.setStyleName("gwt-wyona-main-vp");
        vp.setSpacing(15);
        RootPanel.get("access-policy-editor-hook").add(vp);

        HorizontalPanel searchFilterPanel = new HorizontalPanel();
        searchFilterPanel.setStyleName("gwt-wyona-search-hp");
        vp.add(searchFilterPanel);

        searchFilterPanel.add(new Label(I18n.getLabel("search-box-label", language)));

        final TextBox searchTB = new TextBox();
        searchTB.setVisibleLength(30);
        searchFilterPanel.add(searchTB);

        // Search functionality
        searchTB.addKeyboardListener(
                new KeyboardListenerAdapter() {
                    public void onKeyUp(Widget sender, char keyCode, int modifiers) {
                        identitiesLBW.listBySearchMatch(searchTB.getText());
                    }
                });

/* TODO: Is this still needed?!
        Button searchButton = new Button("Search User or Group", new ClickListener() {
            public void onClick(Widget sender) {
                int itemCount = identitiesLBW.getListBox().getItemCount();
                for (int i = 0; i < itemCount; i++) {
                    String itemText = identitiesLBW.getListBox().getItemText(i);
                    if (itemText.indexOf(searchTB.getText()) >= 0) Window.alert("Result: " + itemText);
                    
                }
            }
  
        });
        searchFilterPanel.add(searchButton);
*/

        HorizontalPanel hp = new HorizontalPanel();
        vp.add(hp);

	
        HorizontalPanel buttonHP = new HorizontalPanel();
        buttonHP.setSpacing(5);
        vp.add(buttonHP);
        //buttonHP.add(new Button("Apply Policy"));

        // Save Button
        final String savePolicyUrl = GWT.getHostPageBaseURL() + savePolicyURL.replaceAll("&amp;", "&");
        //saveButton = new Button("Save Policy and Exit", new ClickListener() {
        saveButton = new Button("Save/Apply Policy", new ClickListener() {
            public void onClick(Widget sender) {
                final AsynchronousPolicySetter aps = new AsynchronousPolicySetter(savePolicyUrl);
                try {
                    com.google.gwt.http.client.Request request = aps.sendRequest(policyLBW.getItems(), policyLBW.getUseInheritedPolicies());
                    // TODO: Disable button during save (start a timer in order to enable when response has been received)
                    //saveButton.setEnabled(false);
                } catch (Exception e) {
                    Window.alert("Exception: " + e.getMessage());
                }
            }
        });
        saveButton.setStyleName("gwt-wyona-SaveButton");
        buttonHP.add(saveButton);

        // Cancel Button
        final String cancelUrl = cancelURL;
        final boolean cancelURLBaseEqualsHostPageURLFinal = cancelURLBaseEqualsHostPageURL;
        Button cancelButton = new Button("Cancel/Close", new ClickListener() {
            public void onClick(Widget sender) {
                //Window.alert("Redirect to " + cancelUrl);
                if (cancelURLBaseEqualsHostPageURLFinal) {
                    redirectTo(GWT.getHostPageBaseURL() + cancelUrl);
                } else {
                    redirectTo(cancelUrl);
                }
            }
            public native void redirectTo(String url) /*-{
                $wnd.location.href=url;
            }-*/; 
        });
        saveButton.setStyleName("gwt-wyona-CancelButton");
        buttonHP.add(cancelButton);

        //vp.add(new Label("Wyona Access Control Policy (GWT) Editor version 1.0-dev-r47029"));
        vp.add(new Label("Wyona Access Control Policy (GWT) Editor version 1.0-dev-rREVISION"));
        

        AddRemoveIdentitiesWidget ariw = new AddRemoveIdentitiesWidget(identitiesLBW, policyLBW.getListBox(), policyLBW);
        ariw.setStyleName("gwt-wyona-AddRemoveWidget");

        CutPasteListOrderWidget cutPasteWidget = new CutPasteListOrderWidget(policyLBW);
        cutPasteWidget.setStyleName("gwt-wyona-CutPasteListOrderWidget");

        hp.add(getFilterUsersAndGroupsButtonsWidget(language));
        hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        hp.add(identitiesLBW);
        hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        hp.add(ariw);
        hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        hp.add(policyLBW);
        hp.add(cutPasteWidget);
    }

    /**
     * Get identities and rights
     * @param url URL to request identities (users and groups) as XML (also see src/contributions/resources/policymanager/src/java/org/wyona/yanel/impl/resources/policymanager/PolicyManagerResource.java, identities-url)
     */
    private void getIdentitiesAndRights(String url) {
        if (allRights == null) {
            //Window.alert("Load users and groups as XML: " + url);
            url = GWT.getHostPageBaseURL() + url.replaceAll("&amp;", "&");
            //Window.alert("Load IdentitiesAndRights: "+ url);
            final AsynchronousIdentitiesAndRightsGetter ag = new AsynchronousIdentitiesAndRightsGetter(url);

            try {
                final com.google.gwt.http.client.Request request = ag.execute();
                
                // Start new thread
                Timer t = new Timer() {
                    public void run() {
                        if (request.isPending()) {
                            rightsIdentitiesRetrievalCompleted = false;
                            identitiesLBW.displayLoadingMessage();
                            scheduleRepeating(10);
                        } else {
                            rightsIdentitiesRetrievalCompleted = true;
                            
                            allRights = ag.getRights();
                            String[] identitiesAllUsers = ag.getUsers();
                            String[] identitiesAllGroups = ag.getGroups();
                            this.cancel();
                            // NOTE: Please note that the server might not provide any groups and hence the OR instead the AND!
                            if (allRights.length > 0 && (identitiesAllUsers.length > 0 || identitiesAllGroups.length > 0)) {
                                policyLBW.set(allRights);
                                identitiesLBW.setUsersAndGroups(identitiesAllUsers, identitiesAllGroups);
                                //Window.alert("Rights and identities have been loaded!" + allRights.length + " " + identitiesAllUsers.length + " " + identitiesAllGroups.length);
                            } else {
                                Window.alert("Rights and identities have not been loaded yet!");
                            }
                        }
                    }
                };
                t.schedule(1);
                
            } catch (Exception e) {
                //if (!com.google.gwt.core.client.GWT.isScript()) {
                e.printStackTrace();
                //}
            }
        } else {
            Window.alert("Rights are already set!");
        }
    }

    /**
     * Get policy
     * @param url URL to request policy as XML
     */
    private void getPolicy(String url) {
        url = GWT.getHostPageBaseURL() + url.replaceAll("&amp;", "&");
        //Window.alert("Load policy as XML: " + url);
        final AsynchronousPolicyGetter apg = new AsynchronousPolicyGetter(url);
        try {
            final com.google.gwt.http.client.Request request = apg.execute();

            // Start new thread
            Timer t = new Timer() {
                public void run() {
                    if (request.isPending() || !rightsIdentitiesRetrievalCompleted) {
                        policyLBW.displayLoading(visibleItemCount);
                        policyRetrievalCompleted = false;
                        scheduleRepeating(10);
                    } else {
                        policyRetrievalCompleted = true;

                        policyUsers = apg.getUsers();
                        policyGroups = apg.getGroups();

                        // "Redraw" Listbox
                        policyLBW.setIdentities(visibleItemCount, policyUsers, policyGroups);

                        useInheritedPolicies = apg.getUseInheritedPolicies();
                        policyLBW.setInheritRightsFlag(useInheritedPolicies);

                        this.cancel();
                        
                        // Remove/Subtract policy-users/groups from "Identities"
                        
                        // Remove all users from identities list, which already exist within the policy
                        for (int i = 0; i < policyUsers.length; i++) {
                            identitiesLBW.removeUser(policyUsers[i].getId());
                        }

                        // Remove all groups from identities list, which already exist within the policy
                        for (int i = 0; i < policyGroups.length; i++) {
                            identitiesLBW.removeGroup(policyGroups[i].getId());
                        }

                        identitiesLBW.listAll(); // TODO: Update display inside removeUser() and removeGroup()

                        //Window.alert("Policy has been loaded!");
                    }
                }
            };
            t.schedule(1);

        } catch (Exception e) {
             Window.alert("Exception: " + e.getMessage());
             //if (!com.google.gwt.core.client.GWT.isScript()) {
             e.printStackTrace();
             //}
        }
    }

    /**
     * Get filter users and groups buttons widget
     * @param language I18n language
     */
    private Widget getFilterUsersAndGroupsButtonsWidget(String language) {
        VerticalPanel vp = new VerticalPanel();
        vp.setStyleName("gwt-wyona-identity-initials-search");
        vp.addStyleName("gwt-wyona-users-and-groups-buttons-vp");

        Button allButton = new Button(I18n.getLabel("button-all", language), new ClickListener() {
                public void onClick(Widget sender) {
                    //Window.alert("DEBUG: Widget clicked with title: " + sender.getTitle());
                    identitiesLBW.listAll();
                }
        });
        allButton.setTitle("All groups button");
        vp.add(allButton);

        vp.add(new AlphabeticalFilterButton('a', 'd', identitiesLBW));
        vp.add(new AlphabeticalFilterButton('e', 'i', identitiesLBW));
        vp.add(new AlphabeticalFilterButton('j', 'm', identitiesLBW));
        vp.add(new AlphabeticalFilterButton('n', 'q', identitiesLBW));
        vp.add(new AlphabeticalFilterButton('r', 'u', identitiesLBW));
        vp.add(new AlphabeticalFilterButton('v', 'z', identitiesLBW));

        return vp;
    }
}



/**
 *
 */
class AddRemoveClickListener implements ClickListener {
     private ListBox lb;

     public AddRemoveClickListener (ListBox lb) {
         this.lb = lb;
     }

     public void onClick(Widget sender) {
         Window.alert("Hello DEBUG");
     }
}
