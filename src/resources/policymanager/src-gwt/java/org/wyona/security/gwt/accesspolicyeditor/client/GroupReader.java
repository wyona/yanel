package org.wyona.security.gwt.accesspolicyeditor.client;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

/* GWT has java.text not implemented. Also see http://groups.google.com/group/Google-Web-Toolkit/msg/abdefa142fb03735
import java.text.DateFormat;
import java.text.SimpleDateFormat;
*/

/**
 * Get group from remote server, whereas overwrite method handleGroupRead()
 */
public abstract class GroupReader {

    private String url;
    private Document doc;
    private boolean pending;

    /**
     * @param url URL to request identities XML document from
     */
    protected GroupReader(String url) {
        this.url = url;
        doc = null;
        pending = true;
    }

    /**
     * Get URL from which groups are being retrieved
     */
    public String getURL() {
        return url;
    }

    /**
     * Get users
     */
    private User[] getUsers() {
        if (doc != null) {
            NodeList userElements = doc.getElementsByTagName("user");
            User[] users = new User[userElements.getLength()];
            for (int i = 0, n = userElements.getLength(); i < n; i++) {
                Element userElement = (Element) userElements.item(i);
                users[i] = new User(userElement.getAttribute("id"), null);
            }
            return users;
        }
        Window.alert("No document containing loaded yet: " + url);
        return null;
    }

    /**
     * Get groups
     */
    private Group[] getGroups() {
        if (doc != null) {
            Element membersElement = (Element) doc.getElementsByTagName("members").item(0);
            if (membersElement != null) {
                NodeList groupElements = membersElement.getElementsByTagName("group");
                Group[] groups = new Group[groupElements.getLength()];
                for (int i = 0, n = groupElements.getLength(); i < n; i++) {
                    Element groupElement = (Element) groupElements.item(i);
                    groups[i] = new Group(groupElement.getAttribute("id"), null);
                }
                return groups;
            } else {
                Window.alert("GroupReader#getGroups() - DEBUG: No members.");
                return null;
            }
        }
        Window.alert("No document containing loaded yet: " + url);
        return null;
    }

    /**
     * Get parent groups
     */
    private Group[] getParents() {
        if (doc != null) {
            Element parentsElement = (Element) doc.getElementsByTagName("parents").item(0);
            if (parentsElement != null) {
                NodeList groupElements = parentsElement.getElementsByTagName("group");
                Group[] groups = new Group[groupElements.getLength()];
                for (int i = 0, n = groupElements.getLength(); i < n; i++) {
                    Element groupElement = (Element) groupElements.item(i);
                    groups[i] = new Group(groupElement.getAttribute("id"), null);
                }
                return groups;
            } else {
                //Window.alert("DEBUG: No parents.");
                return null;
            }
        }
        Window.alert("No document containing loaded yet: " + url);
        return null;
    }

    /**
     * Check whether document is pending or not
     */
    private boolean isPending() {
        return pending;
    }

    /**
     * Set group document.
     * @param doc XML document containing group
     */
    private void setDocument(Document doc) {
        this.doc = doc;
        if (doc != null) {
            pending = false;
        } else {
            pending = true;
        }
    }

    /**
     * Get XML document containing identities from remote server.
     */
    private void loadDocument() {
        setDocument(null);
        //Window.alert("Load identities from URL: " + url);
        RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, url);
        try {
            rb.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    setDocument(XMLParser.parse(response.getText()));
                }
                @Override
                public void onError(Request request, Throwable exception) {
                    throw new RuntimeException(exception);
                }
            });
        } catch (RequestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read the group
     */
    public final void read() {
        final GroupReader groupReader = this;
        groupReader.loadDocument();
        Timer t = new Timer() {
            @Override
            public void run() {
                if (groupReader.isPending()) {
                    scheduleRepeating(10);
                } else {
                    this.cancel();
                    //Window.alert("DEBUG: Users loaded.");
                    groupReader.handleGroupRead(getUsers(), getGroups(), getParents());
                }
            }
        };
        t.schedule(1);
    }

    /**
     * Overwrite this method in order to access the read group
     */
    protected abstract void handleGroupRead(User[] users, Group[] groups, Group[] parentGroups);
}
