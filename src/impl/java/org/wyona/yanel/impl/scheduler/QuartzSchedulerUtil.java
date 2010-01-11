package org.wyona.yanel.impl.scheduler;

import org.w3c.dom.Document;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 *
 */
public class QuartzSchedulerUtil {

    private static Logger log = Logger.getLogger(QuartzSchedulerUtil.class);

    /**
     * Schedule jobs based on XML configuration
     * @param scheduler Scheduler
     * @param doc XML document containing jobs configuration
     * @param groupName Group name, e.g. realm ID
     */
    public static void schedule(Scheduler scheduler, Document doc, String groupName) throws Exception {
        log.warn("Add jobs for group '" + groupName + "' to scheduler.");

        org.w3c.dom.NodeList jobElements = doc.getDocumentElement().getElementsByTagName("job");
   
        for (int i = 0; i < jobElements.getLength(); i++) {
            org.w3c.dom.Element jobE = (org.w3c.dom.Element) jobElements.item(i);
            log.warn("Add job with class: " + jobE.getAttribute("class"));
            JobDetail jobDetail = new JobDetail(jobE.getAttribute("name"), groupName, Class.forName(jobE.getAttribute("class")));
            Date startDate = new Date();
            Date endDate = null;
            Trigger trigger = new SimpleTrigger("heartbeatTrigger", groupName, startDate, endDate, SimpleTrigger.REPEAT_INDEFINITELY, 60L * 1000L);
            scheduler.scheduleJob(jobDetail, trigger);
        }
    }
}
