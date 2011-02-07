package org.wyona.yanel.servlet;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.apache.log4j.Logger;

import org.wyona.yanel.core.map.Realm;

/**
 * Heartbeat job
 */
public class HeartbeatJob implements Job {

    private static Logger log = Logger.getLogger(HeartbeatJob.class);

    /**
     *
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Realm realm = (Realm) context.getJobDetail().getJobDataMap().get("realm");
        String realmName = null;
        if (realm != null) {
            realmName = realm.getName();
        }
        log.info("Heartbeat: " + new java.util.Date() + " (Realm: " + realmName + ")"); // TODO: Show statistics, e.g. uptime, etc.
        //log.debug("Heartbeat: " + new java.util.Date() + " (Realm: " + realmName + ")");
    }
}
