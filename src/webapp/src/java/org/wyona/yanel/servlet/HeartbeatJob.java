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
     * @see org.quartz.Job#execute(JobExecutionContext)
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Realm realm = (Realm) context.getJobDetail().getJobDataMap().get("realm");
        String realmName = null;
        if (realm != null) {
            realmName = realm.getName();
        }
        String description = context.getJobDetail().getDescription();
        log.info("Heartbeat: " + new java.util.Date() + " (Realm: " + realmName + ", Description: " + description + ")"); // TODO: Show statistics, e.g. uptime, etc.
    }
}
