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

package org.wyona.yanel.core.api.attributes;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.wyona.yanel.core.Path;

/**
 * DEV (not released yet), please be aware that this interface still might change ...
 */
public interface ModifiableV2 {

    /**
     *
     */
    public Reader getReader() throws Exception;

    /**
     *
     */
    public InputStream getInputStream() throws Exception;
    
    /**
     *
     */
    public Writer getWriter() throws Exception;

    /**
     *
     */
    public OutputStream getOutputStream() throws Exception;

    /**
     * To write data and allowing the resource to modify data during writing, 
     * e.g. "updated" element of atom entry
     */
    public void write(InputStream in) throws Exception;
    
    /**
     *
     */
    public long getLastModified() throws Exception;

    /**
     *
     */
    public boolean delete() throws Exception;
}
