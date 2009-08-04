package org.wyona.yanel.impl.resources.search;

/**
 *
 */
public class Result {

    private String url;
    private String title;

    /**
     *
     */
    public Result(String url, String title) {
        this.url = url;
        this.title = title;
    }

    /**
     * Get URL
     */
     public String getURL() {
         return url;
     }

    /**
     * Get title
     */
     public String getTitle() {
         return title;
     }
}
