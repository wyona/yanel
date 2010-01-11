package org.wyona.yanel.impl.scheduler;

import org.w3c.dom.Document;

import org.quartz.Scheduler;

import org.apache.log4j.Logger;

/**
 *
 */
public class QuartzSchedulerUtil {

    private static Logger log = Logger.getLogger(QuartzSchedulerUtil.class);

    /**
     *
     */
    public static void schedule(Scheduler scheduler, Document doc, String groupName) {
        log.warn("Add jobs for group '" + groupName + "' to scheduler.");
    }
}
