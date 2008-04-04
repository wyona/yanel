package org.wyona.yanel.core.source;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

/**
 */
public class YanelStreamSource extends StreamSource {
    private static Logger log = Logger.getLogger(YanelStreamSource.class);

    protected long lastModified = -1;
    
    public YanelStreamSource() {
    }
    
    public YanelStreamSource(File f) {
        super(f);
        this.lastModified = f.lastModified();
    }

    public YanelStreamSource(InputStream inputStream, String systemId) {
        super(inputStream, systemId);
    }

    public YanelStreamSource(InputStream inputStream) {
        super(inputStream);
    }

    public YanelStreamSource(Reader reader, String systemId) {
        super(reader, systemId);
    }

    public YanelStreamSource(Reader reader) {
        super(reader);
    }

    public YanelStreamSource(String systemId) {
        super(systemId);
    }

    /**
     * Gets the last modification date of this source.
     * @return last modification date in ms or -1 if it's unknown.
     */
    public long getLastModified() {
        return this.lastModified;
    }
    
    /**
     * Sets the last modification date of this source.
     * Only the creator of this source should call this method.
     * @param lastModified last modification date in ms
     */
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
}
