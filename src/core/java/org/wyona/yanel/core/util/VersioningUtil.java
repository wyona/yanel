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
import org.wyona.yanel.core.api.attributes.WorkflowableV1;
import org.wyona.yanel.core.attributes.versionable.RevisionInformation;

import org.apache.log4j.Logger;

/**
 * Utility class in order to roll-back to a particular revision
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
                    if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Workflowable", "1")) {
                        WorkflowableV1 workflowableRes = (WorkflowableV1) resource;
                        // TBD: Should the workflow state of the old revision be used (as is) or rather workflow.getInitialState()
                        //log.debug("Revision head: " + getRevisionHead(versionableRes));
                        workflowableRes.setWorkflowState(workflowableRes.getWorkflowState(revisionName), getRevisionHead(versionableRes));
                    } else {
                        log.info("Cannot set workflow, because resource '" + resource.getPath() + "' is not WorkflowableV1");
                    }
                } else {
                    log.warn("Resource is already checked out by user '" + versionableRes.getCheckoutUserID() + "' and hence cannot be rolled back at the moment!");
                }
            } catch(Exception e) {
                log.error(e, e);
            }
        } else {
            log.warn("Cannot be rolled back, because resource '" + resource.getPath() + "' is not VersionableV2");
        }
    }

    /**
     * Get revision number/name of head (most recent) revision (WARN: Peformance/Scalability)
     */
    private static String getRevisionHead(VersionableV2 resource) throws Exception {
        RevisionInformation[] ri = resource.getRevisions();
        java.util.Arrays.sort(ri);
        return ri[0].getName();
    }
}
