package org.wyona.yanel.core.source;

import javax.xml.transform.TransformerException;

/**
 * 
 */
public class SourceException extends TransformerException {

    /**
     *
     */
    //public SourceException() {
        //super();
    //}

    /**
     *
     */
    public SourceException(Throwable t) {
        super(t);
    }

    /**
     *
     */
    public SourceException(String s) {
        super(s);
    }

    /**
     *
     */
    public SourceException(String s, Throwable t) {
        super(s, t);
    }

}
