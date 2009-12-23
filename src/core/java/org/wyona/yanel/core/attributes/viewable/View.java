/*
 * Copyright 2006 Wyona
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.wyona.org/licenses/APACHE-LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wyona.yanel.core.attributes.viewable;

import java.io.InputStream;
import java.util.HashMap;

/**
 * A resource type can return various views of the same object
 */
public class View {

    private String mt;
    private String encoding = null;
    private InputStream is;
    private boolean isResponse = true;
    private HashMap httpHeaders = null;

    public View() {
        this.httpHeaders = new HashMap();
    }
    
    /**
     *
     */
    public void setMimeType(String mt) {
        this.mt = mt;
    }

    /**
     *
     */
    public String getMimeType() {
        return mt;
    }

    /**
     *
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     *
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Set content of response as InputStream
     */
    public void setInputStream(InputStream is) {
        this.is = is;
    }

    /**
     * Get content of response as InputStream
     */
    public InputStream getInputStream() {
        return is;
    }

    /**
     * Checks if this view contains the content of the response itself and if so, then Yanel can use getInputStream() to write the response.
     * The default is true. If false, then it means the resource has written the response directly and getInputStream() should contain no data.
     */
    public boolean isResponse() {
        return isResponse;
    }

    /**
     * Set whether this view will be used or not by Yanel to write the response.
     * Resources which want to write the response themselves should set this to false.
     * If this method is not called, then isResponse() will return true as default, which assumes that this view contains the response.
     */
    public void setResponse(boolean isResponse) {
        this.isResponse = isResponse;
    }
    
    public HashMap getHttpHeaders() {
        return this.httpHeaders;
    }
    
    public void setHttpHeaders(HashMap headers) {
        this.httpHeaders.putAll(headers);
    }
    
    public void setHttpHeader(String name, String value) {
        this.httpHeaders.put(name, value);
    }
}
