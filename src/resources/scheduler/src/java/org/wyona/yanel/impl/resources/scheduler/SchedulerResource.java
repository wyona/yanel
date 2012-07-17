/*
 * Copyright 2012 Wyona
 */

package org.wyona.yanel.impl.resources.scheduler;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;

/**
 * Scheduler jobs overview
 */
public class SchedulerResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(SchedulerResource.class);
    
    /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    @Override
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        java.util.Collection<Scheduler> schedulers = new org.quartz.impl.StdSchedulerFactory().getAllSchedulers();

        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<scheduler>");
        if (schedulers == null || schedulers.isEmpty()) {
            sb.append("<no-schedulers-running/>");
        } else {
            sb.append("<number-of-schedulers>" + schedulers.size() + "</number-of-schedulers>");
            java.util.Iterator schedulerIt  = schedulers.iterator();
            Scheduler scheduler = (Scheduler) schedulerIt.next();
            String name = getRealm().getID();
            java.util.Set keys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(name));
            java.util.Iterator keysIt = keys.iterator();
            while(keysIt.hasNext()) {
                JobKey jobKey = (JobKey)keysIt.next();
                JobDetail jd = scheduler.getJobDetail(jobKey);

                java.util.Set triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(name));
                java.util.Iterator triggerKeysIt = triggerKeys.iterator();
                while(triggerKeysIt.hasNext()) {
                    TriggerKey triggerKey = (TriggerKey) triggerKeysIt.next();
                    //log.warn("DEBUG: Check whether trigger's job key '" + scheduler.getTrigger(triggerKey).getJobKey() + "' and job key '" + jobKey + "' match...");
                    if (scheduler.getTrigger(triggerKey).getJobKey().toString().equals(jobKey.toString())) {
                        log.warn("DEBUG: Trigger and job '" + jd.getDescription() + "' matched: " + scheduler.getTrigger(triggerKey).getNextFireTime());
                    }
                }

                sb.append("<job>" + jd.getDescription() + "</job>");
                // TODO: Display scheduled execution time... getTriggers and compare JobKey of each Trigger with current Job and then display getNextFireTime()
            }
        }
        sb.append("</scheduler>");
        return new ByteArrayInputStream(sb.toString().getBytes());
    }
}
