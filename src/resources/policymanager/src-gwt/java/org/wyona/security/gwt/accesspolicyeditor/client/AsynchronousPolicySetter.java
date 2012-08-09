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

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;

/**
 * http://code.google.com/p/bunsenandbeaker/wiki/DevGuideHttpRequests
 * http://code.google.com/p/bunsenandbeaker/wiki/DevGuideXML
 */
public class AsynchronousPolicySetter implements RequestCallback {

    private RequestBuilder requestBuilder = null;

    /**
     *
     */
    public AsynchronousPolicySetter(String url) {
        //Window.alert("Save policy to: " + url);
        requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
    }

    /**
     * Send XML request containing policy with users and groups and corresponding rights
     * @param items A list of users and groups
     */
    public Request sendRequest(java.util.List items, boolean useInheritedPolicies) throws RequestException {
        StringBuffer data = new StringBuffer("<?xml version=\"1.0\"?>");
        data.append("<policy xmlns=\"http://www.wyona.org/security/1.0\" use-inherited-policies=\"" + useInheritedPolicies + "\">");
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                Item item = (Item) items.get(i);
                if (items.get(i) instanceof User) {
                    data.append("<user id=\"" + item.getId() + "\">");
                    Right[] rights = item.getRights();
                    if (rights != null) {
                        for (int k = 0; k < rights.length; k++) {
                            data.append("<right id=\"" + rights[k].getId() + "\" permission=\"" + rights[k].getPermission() + "\">" + rights[k].getId() + "</right>");
                        }
                    } else {
                        // TODO: Do not hardcode rights
                        data.append("<right id=\"r\" permission=\"false\">" + "r" + "</right>");
                        data.append("<right id=\"w\" permission=\"false\">" + "w" + "</right>");
                    }
                    data.append("</user>");
                } else if (items.get(i) instanceof Group) {
                    data.append("<group id=\"" + item.getId() + "\">");
                    Right[] rights = item.getRights();
                    if (rights != null) {
                        for (int k = 0; k < rights.length; k++) {
                            data.append("<right id=\"" + rights[k].getId() + "\" permission=\"" + rights[k].getPermission() + "\">" + rights[k].getId() + "</right>");
                        }
                    } else {
                        // TODO: Do not hardcode rights
                        data.append("<right id=\"r\" permission=\"false\">" + "r" + "</right>");
                        data.append("<right id=\"w\" permission=\"false\">" + "w" + "</right>");
                    }
                    data.append("</group>");
                } else {
                    Window.alert("ERROR: No items!");
                }
            }
        } else {
            Window.alert("ERROR: No items!");
        }
	data.append("</policy>");
        return requestBuilder.sendRequest(data.toString(), this);
    }

    /**
     * @deprecated Use sendRequest(Item[], boolean) instead
     */
/*
    public Request sendRequest(User[] users, Group[] groups, boolean useInheritedPolicies) throws RequestException {
        StringBuffer data = new StringBuffer("<?xml version=\"1.0\"?>");
        data.append("<policy xmlns=\"http://www.wyona.org/security/1.0\" use-inherited-policies=\"" + useInheritedPolicies + "\">");
        if (users != null) {
            for (int i = 0; i < users.length; i++) {
                data.append("<user id=\"" + users[i].getId() + "\">");
                Right[] rights = users[i].getRights();
                if (rights != null) {
                    for (int k = 0; k < rights.length; k++) {
                        data.append("<right id=\"" + rights[k].getId() + "\" permission=\"" + rights[k].getPermission() + "\">" + rights[k].getId() + "</right>");
                    }
                } else {
                    // TODO: Do not hardcode rights
                    data.append("<right id=\"r\" permission=\"false\">" + "r" + "</right>");
                    data.append("<right id=\"w\" permission=\"false\">" + "w" + "</right>");
                }
                data.append("</user>");
            }
        }
        if (groups != null) {
            for (int i = 0; i < groups.length; i++) {
                data.append("<group id=\"" + groups[i].getId() + "\">");
                Right[] rights = groups[i].getRights();
                if (rights != null) {
                    for (int k = 0; k < rights.length; k++) {
                        data.append("<right id=\"" + rights[k].getId() + "\" permission=\"" + rights[k].getPermission() + "\">" + rights[k].getId() + "</right>");
                    }
                } else {
                    // TODO: Do not hardcode rights
                    data.append("<right id=\"r\" permission=\"false\">" + "r" + "</right>");
                    data.append("<right id=\"w\" permission=\"false\">" + "w" + "</right>");
                }
                data.append("</group>");
            }
        }
	data.append("</policy>");
        return requestBuilder.sendRequest(data.toString(), this);
    }
*/

    /**
     *
     */
    public void onResponseReceived(Request request, Response response) {
        if (response.getStatusCode() == 200) {
            Window.alert("Policy has been saved successfully!");
        } else {
            Window.alert("Policy has NOT been saved! Please check log files on server.");
        }
    }

    /**
     *
     */
    public void onError(Request request, Throwable exception) {
        Window.alert("Exception: " + exception.getMessage());
    }
}
