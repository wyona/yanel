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

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Topic;

/**
 * TODO: Shouldn't it be the other way around, that the Resource is passed to a Repository instead the Repository to the Resource?!
 * TODO: Shouldn't we use java.io.Serializable (writeObject, readObject)?!
 */
public interface WritableResource {

    /**
     *
     */
    public void write(Path path);

    /**
     *
     */
    public void write(Topic topic);
}
