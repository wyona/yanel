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

package org.wyona.yanel.core.attributes;

/**
 *
 */
public interface CreatableV1 {

    /**
     * TODO: Wouldn't it be better to implement this with introspection?
     */
    public String[] getPropertyNames();

    /**
     * TODO: Wouldn't it be better to implement this with a Setter interface?
     */
    public void setProperty(String name, Object value);

    /**
     * TODO: Wouldn't it be better to implement this with a Getter interface?
     */
    public Object getProperty(String name);
}
