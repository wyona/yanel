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

import org.wyona.yanel.gwt.client.AsynchronousAgent;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.user.client.Window;

import java.util.Vector;

/**
 *
 */
public class AsynchronousPolicyGetter extends AsynchronousAgent {

    boolean useInheritedPolicies = true;
    Vector users = new Vector();
    Vector groups = new Vector();

    /**
     *
     */
    public AsynchronousPolicyGetter(String url) {
        super(url);
    }

    /**
     * See src/gallery/src/java/org/wyona/yanel/gwt/client/ui/gallery/AsynchronousGalleryBuilder.java
     * Also see src/access-policy-editor/java/org/wyona/security/gwt/accesspolicyeditor/public/sample-policy.xml
     */
    public void onResponseReceived(final Request request, final Response response) {
        Element rootElement = XMLParser.parse(response.getText()).getDocumentElement();
        //Window.alert("Root element: " + rootElement.getTagName());

	// Get use-inherited-policies attribute
        String useInheritedPoliciesString = rootElement.getAttribute("use-inherited-policies");
        if (useInheritedPoliciesString == null) {
            useInheritedPolicies = true;
        } else {
            if (useInheritedPoliciesString.equals("false")) {
                useInheritedPolicies = false;
            } else {
                useInheritedPolicies = true;
            }
        }
        //Window.alert("use-inherited-policies: " + useInheritedPoliciesString);

        // TODO: Parse rights and use labels for formatting, e.g. "u: (Read,Write) benjamin", "u: (Read,-) susi"

        Element worldElement = getFirstChildElement(rootElement, "world");
        if (worldElement != null) {
            // TODO: ...
            //identities.add("WORLD (Read,Write)");
            //Window.alert("World: " + (String) identities.elementAt(identities.size() - 1));
        }

        NodeList userElements = rootElement.getElementsByTagName("user");
        for (int i = 0; i < userElements.getLength(); i++) {
            Element userE = (Element) userElements.item(i);

            NodeList rightElements = userE.getElementsByTagName("right");
            Right[] rights = new Right[rightElements.getLength()];
            for (int k = 0; k < rights.length; k++) {
                Element rightE = (Element) rightElements.item(k);
                // TODO: Do not hardcode permission
                rights[k] = new Right(rightE.getAttribute("id"), true);
                //Window.alert("User Right: " + rights[k].getId());
            }

            users.add(new User(userE.getAttribute("id"), rights));
            //Window.alert("User: " + ((User) users.elementAt(users.size() - 1)).getId());
        }

        NodeList groupElements = rootElement.getElementsByTagName("group");
        for (int i = 0; i < groupElements.getLength(); i++) {
            Element groupE = (Element) groupElements.item(i);

            NodeList rightElements = groupE.getElementsByTagName("right");
            Right[] rights = new Right[rightElements.getLength()];
            for (int k = 0; k < rights.length; k++) {
                Element rightE = (Element) rightElements.item(k);
                // TODO: Do not hardcode permission
                rights[k] = new Right(rightE.getAttribute("id"), true);
                //Window.alert("Group Right: " + rights[k].getId());
            }

            groups.add(new Group(groupE.getAttribute("id"), rights));
            //Window.alert("Group: " + ((Group) groups.elementAt(groups.size() - 1)).getId());
        }

        //Window.alert("Policy response processed!");
    }

    /**
     * Get users from access policy
     */
    public User[] getUsers() {
        User[] ids = new User[users.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = (User)users.elementAt(i);
        }
        return ids;
    }

    /**
     * Get groups from access policy
     */
    public Group[] getGroups() {
        Group[] ids = new Group[groups.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = (Group)groups.elementAt(i);
        }
        return ids;
    }

    /**
     * Get flag use-inherited-policies
     */
    public boolean getUseInheritedPolicies() {
        return useInheritedPolicies;
    }

    /**
     *
     */
    private Element getFirstChildElement(Element parent, String name) {
        NodeList nl = parent.getElementsByTagName(name);
        if (nl.getLength() > 0) {
            return (Element) nl.item(0);
        } else {
            return null;
        }
    }

    public void onError(Request arg0, Throwable arg1) {
        // TODO Auto-generated method stub
        
    }
}
