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

package org.wyona.yanel.core;

import org.apache.log4j.Category;

/**
 *
 */
public class Path extends org.wyona.commons.io.Path {

    private static Category log = Category.getInstance(Path.class);

    /**
     *
     */
    public Path(String path) {
        super(path);
    }

    /**
     *
     */
    public Path getRTIPath() {
        // Remove trailing slash except for ROOT ...
        if (path.length() > 1 && path.charAt(path.length() - 1) == '/') {
            return new Path(path.substring(0, path.length() - 1) + ".yanel-rti");
        }
        return new Path(path + ".yanel-rti");
    }
}
