package org.wyona.yanel.core.attributes.tracking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Bean containing tracking information such as tags, page type, request action, etc.
 */
public class TrackingInformationV1 {

    private List<String> tags = new ArrayList();
    private String pageType;
    private String requestAction;
    private HashMap<String, String> hm = new HashMap();

    /**
     * Get tags
     */
    public String[] getTags() {
        return (String[])tags.toArray(new String[tags.size()]);
    }

    /**
     * Add tag
     */
    public void addTag(String tag) {
        tags.add(tag);
    }

    /**
     * Get page type
     */
    public String getPageType() {
        return pageType;
    }

    /**
     * Set page type
     */
    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    /**
     * Get request action
     */
    public String getRequestAction() {
        return requestAction;
    }

    /**
     * Set request action
     */
    public void setRequestAction(String requestAction) {
        this.requestAction = requestAction;
    }

    /**
     * Get custom key/value pairs
     */
    public HashMap<String, String> getCustomFields() {
        return hm;
    }

    /**
     * Add custom key/value pair
     * @param key Key/Name with which the specified value is to be associated.
     * @param value Value to be associated with the specified key.
     */
    public void addCustomField(String key, String value) {
        hm.put(key, value);;
    }
}
