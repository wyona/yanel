package org.wyona.yanel.impl.resources.search;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 *
 */
public class Result {

    private static Logger log = Logger.getLogger(Result.class);

    private String url;
    private String title;
    private String desc;
    private String contentType;
    private Date lastModified;

    /**
     * @param desc Description
     */
    public Result(String url, String title, String desc, String contentType, Date lastModified) {
        this.url = url;
        log.debug("Title: '" + title + "'");
        this.title = title;
        this.desc = desc;
        this.contentType = contentType;
        this.lastModified = lastModified;
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

    /**
     * Get description
     */
     public String getDescription() {
         return desc;
     }

    /**
     * Get content type (for example text/html, application/xhtml+xml, etc.)
     */
     public String getContentType() {
         return contentType;
     }

    /**
     * Get last modified date
     */
     public Date getLastModified() {
         return lastModified;
     }
}
