package org.wyona.yanel.core.api.attributes;

import org.wyona.yanel.core.attributes.tracking.TrackingInformationV1;

/**
 * DEV (not released yet), please be aware that this interface still might change ...
 *
 * Interface to set/get tracking information which for example can be processed by Boost
 */
public interface TrackableV1 {

    /**
     * Provide a bean to set/get tracking information
     * @param info Tracking information bean
     */
    public void doTrack(TrackingInformationV1 info);
}
