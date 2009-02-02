/*
 * Copyright 2009 Wyona
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

package org.wyona.yanel.core.util;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ModifiableV2;
import org.wyona.yanel.core.api.attributes.VersionableV2;
import org.wyona.yanel.core.attributes.versionable.RevisionInformation;

import org.apache.log4j.Logger;

/**
 *
 */
public class VersioningUtil {

    private static Logger log = Logger.getLogger(VersioningUtil.class);

    /**
     * @param resource Resource which shall be rolled back
     * @param revisionName Revision name to which shall be rolled back
     */
    public static void rollBack(Resource resource, String revisionName) {
        log.error("DEBUG: Implementation not finished yet!");
    }
}
