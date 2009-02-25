/*
 * Copyright 2007-2009 Wyona
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

package org.wyona.yanel.impl.resources.updatefinder.utils;

import java.util.Comparator;
import java.util.Map;

public class UpdateInfoVersionComparator implements Comparator<Map<String, String>> {
    
    public int compare(Map<String, String> info, Map<String, String> anotherInfo) {
        String version = info.get("version");
        String anotherVersion = anotherInfo.get("version");
        VersionComparator versionComparator = new VersionComparator();
        return versionComparator.compare(version, anotherVersion);
    }
}