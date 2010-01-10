package org.wyona.yanel.servlet;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.apache.log4j.Logger;

/**
 * Heartbeat job
 */
public class HeartbeatJob implements Job {

    private static Logger log = Logger.getLogger(HeartbeatJob.class);

    /**
     *
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Heartbeat: " + new java.util.Date()); // TODO: Show statistics, e.g. uptime, etc.
    }
}
