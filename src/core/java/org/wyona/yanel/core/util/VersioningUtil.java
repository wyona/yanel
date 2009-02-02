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
     * @param userName User who is executing the roll back
     */
    public static void rollBack(Resource resource, String revisionName, String userName) {
        if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Versionable", "2")) {
            try {
                VersionableV2 versionableRes = (VersionableV2) resource;
                if (!versionableRes.isCheckedOut()) {
                    versionableRes.checkout(userName);
                    versionableRes.restore(revisionName);
                    versionableRes.checkin("Rolled back to revision '" + revisionName + "'");
                } else {
                    log.warn("Resource is already checked out by user '" + versionableRes.getCheckoutUserID() + "' and hence cannot be rolled back at the moment!");
                }
            } catch(Exception e) {
                log.error(e, e);
            }
        } else {
            log.warn("Cannot be rolled back, because resource is not VersionableV2");
        }
    }
}
