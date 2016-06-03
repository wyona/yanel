package org.wyona.yanel.servlet;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.apache.log4j.Logger;
import org.wyona.yanel.core.map.Realm;

/**
 * Heartbeat job example that will not be executed again if one is already running. 
 * Use this example for jobs that can take a lot more time than the scheduled time (e.g. job might run for 4h, but we let it run every 30min if it is not already running)
 */
@DisallowConcurrentExecution
public class HeartbeatJobNonConcurrent implements Job {

    private static Logger log = Logger.getLogger(HeartbeatJobNonConcurrent.class);

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
