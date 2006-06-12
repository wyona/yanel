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

package com.wyonapictures.yanel.impl.resources;

import org.wyona.yanel.core.ResourceTypeDefinition;
import org.wyona.yanel.core.api.attributes.CreatableV1;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.wyona.yanel.impl.ResourceDefaultImpl;

/**
 *
 */
public class TapeResource extends ResourceDefaultImpl implements CreatableV1, ViewableV1 {

    /**
     *
     */
    public TapeResource() {
    }

    /**
     *
     */
    public String[] getPropertyNames() {
        String[] pn = {"name", "description", "video-format", "audio-format", "content"};
        return pn;
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        return null;
    }
}
