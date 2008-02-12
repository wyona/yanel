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

import javax.servlet.http.HttpServletRequest;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.api.attributes.CreatableV1;

import java.util.HashMap;

/**
 * DEV (not released yet), please be aware that this interface still might change ...
 */
public interface CreatableV2 extends CreatableV1 {

    public static String TYPE_UPLOAD = "type_upload";
    public static String TYPE_STRING = "type_string";
    public static String TYPE_SELECT = "type_select";
    public static String TYPE_PASSWORD = "type_password";

    /**
     * Get property type which is intended to be used for the different types of (XHTML) input fields, e.g. TYPE_UPLOAD, TYPE_STRING (also see CreatableV1.getPropertyNames()
     */
    public String getPropertyType(String propertyName);

    /**
     * Creates the resource
     */
    public void create(HttpServletRequest request);

    /**
     * Get resource configuration properties which shall be used for the new resource configuration of the new resource
     */
    public HashMap createRTIProperties(HttpServletRequest request);

    /**
     * Allows overwriting the name for the new resource which is suggested by Yanel or rather by the user input. This is useful if one wants to dynamically generate names which are for instance based on a timestamp. Return null if the resource shall not be associated with a resource configuration. This can useful for resources which are used "internally", e.g. the Yanel-User resource.
     */
    public String getCreateName(String suggestedName);
}
