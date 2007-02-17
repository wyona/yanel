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

import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import java.io.OutputStream;

/**
 * DEV (not released yet), please be aware that this interface still might change ...
 */
public interface ViewableV2 {

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors();

    public View getView(String viewId) throws Exception;
    
    /**
     * NOTE: A resource does not necessarily have a path
     */
    //public View getView(Path path, OutputStream out, String viewId) throws Exception;

    /**
     * 
     */
    public boolean exists() throws Exception;
    
    /**
     * 
     */
    public long getSize() throws Exception;
    
    /**
     * 
     */
    public String getMimeType(String viewId) throws Exception;
}
