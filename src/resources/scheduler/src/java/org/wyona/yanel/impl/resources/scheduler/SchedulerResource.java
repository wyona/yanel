/*
 * Copyright 2012 Wyona
 */

package org.wyona.yanel.impl.resources.scheduler;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

import org.wyona.commons.xml.XMLHelper;

/**
 * Scheduler jobs overview
 */
public class SchedulerResource extends BasicXMLResource {
    
    private static Logger log = LogManager.getLogger(SchedulerResource.class);
    
    /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    @Override
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        java.util.Collection<Scheduler> schedulers = new org.quartz.impl.StdSchedulerFactory().getAllSchedulers();

        Document doc = XMLHelper.createDocument("http://www.wyona.org/yanel/scheduler/1.0.0", "scheduler");
        Element rootEl = doc.getDocumentElement();
        rootEl.setAttribute("current-time", "" + new java.util.Date());

        if (schedulers == null || schedulers.isEmpty()) {
            Element noSchedulerRunningEl = (Element) rootEl.appendChild(doc.createElement("no-schedulers-running"));
            noSchedulerRunningEl.appendChild(doc.createTextNode("Scheduler is probably disabled inside yanel.xml"));
        } else {
            Element numberOfSchedulersEl = (Element)rootEl.appendChild(doc.createElement("number-of-schedulers"));
            numberOfSchedulersEl.appendChild(doc.createTextNode("" + schedulers.size()));
            java.util.Iterator schedulerIt  = schedulers.iterator();
            Scheduler scheduler = (Scheduler) schedulerIt.next();
            String name = getRealm().getID();
            java.util.Set keys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(name));
            java.util.Iterator keysIt = keys.iterator();
            while(keysIt.hasNext()) {
                JobKey jobKey = (JobKey)keysIt.next();
                JobDetail jd = scheduler.getJobDetail(jobKey);

                Element jobEl = (Element)rootEl.appendChild(doc.createElement("job"));
                jobEl.appendChild(doc.createTextNode(jd.getDescription()));

                java.util.Set triggerKeys = scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(name));
                java.util.Iterator triggerKeysIt = triggerKeys.iterator();
                while(triggerKeysIt.hasNext()) {
                    TriggerKey triggerKey = (TriggerKey) triggerKeysIt.next();
                    //log.debug("Check whether trigger's job key '" + scheduler.getTrigger(triggerKey).getJobKey() + "' and job key '" + jobKey + "' match...");
                    if (scheduler.getTrigger(triggerKey).getJobKey().toString().equals(jobKey.toString())) {
                        log.debug("Trigger and job '" + jd.getDescription() + "' matched: " + scheduler.getTrigger(triggerKey).getNextFireTime());
                        jobEl.setAttribute("next-fire-time", "" + scheduler.getTrigger(triggerKey).getNextFireTime());
                    }
                }
            }
        }

        return XMLHelper.getInputStream(doc, false, true, null);
    }
}
