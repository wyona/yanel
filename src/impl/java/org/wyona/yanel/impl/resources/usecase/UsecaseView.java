package org.wyona.yanel.impl.resources.usecase;

import java.io.InputStream;

import org.apache.log4j.Category;

/**
 * @deprecated
 */
public class UsecaseView {

    private static Category log = Category.getInstance(UsecaseView.class);

    /**
     * @deprecated
     */
    public void setMimeType(String mt) {
        log.error("DEPRECATED!");
    }

    /**
     * @deprecated
     */
    public String getMimeType() {
        log.error("DEPRECATED!");
        return null;
    }

    /**
     * @deprecated
     */
    public void setEncoding(String encoding) {
        log.error("DEPRECATED!");
    }

    /**
     * @deprecated
     */
    public String getEncoding() {
        log.error("DEPRECATED!");
        return null;
    }

    /**
     * @deprecated
     */
    public void setInputStream(InputStream is) {
        log.error("DEPRECATED!");
    }

    /**
     * @deprecated
     */
    public InputStream getInputStream() {
        log.error("DEPRECATED!");
        return null;
    }

    /**
     * @deprecated
     */
    public boolean isResponse() {
        log.error("DEPRECATED!");
        return false;
    }

    /**
     * @deprecated
     */
    public void setResponse(boolean isResponse) {
        log.error("DEPRECATED!");
    }

    /**
     * @deprecated
     */
    public String getID() {
        log.error("DEPRECATED!");
        return null;
    }
}
