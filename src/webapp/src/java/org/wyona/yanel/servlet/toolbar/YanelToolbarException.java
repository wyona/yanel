package org.wyona.yanel.servlet.toolbar;

/**
 * An exception indicating a problem generating the Yanel toolbar markup.
 */
public class YanelToolbarException extends RuntimeException {

    public YanelToolbarException() {
    }

    public YanelToolbarException(String message) {
        super(message);
    }

    public YanelToolbarException(Throwable cause) {
        super(cause);
    }

    public YanelToolbarException(String message, Throwable cause) {
        super(message, cause);
    }

}
