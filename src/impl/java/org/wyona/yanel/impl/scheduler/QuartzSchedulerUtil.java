package org.wyona.yanel.impl.scheduler;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import java.util.Date;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
        log.info("Add jobs for group '" + groupName + "' to scheduler.");

        Element jobsElement = (Element) doc.getDocumentElement().getElementsByTagName("jobs").item(0);
        String enabled = jobsElement.getAttribute("enabled");
        if (enabled != null && enabled.equals("false")) {
            log.warn("Jobs of group/realm '" + groupName + "' have been disabled.");
            return;
        }

        NodeList jobElements = doc.getDocumentElement().getElementsByTagName("job");
   
        for (int i = 0; i < jobElements.getLength(); i++) {
            Element jobE = (Element) jobElements.item(i);
            log.info("Add job with class: " + jobE.getAttribute("class"));
            JobDetail jobDetail = new JobDetail(jobE.getAttribute("name"), groupName, Class.forName(jobE.getAttribute("class")));
            Date startDate = new Date();
            Date endDate = null;
            Trigger trigger = new SimpleTrigger("heartbeatTrigger", groupName, startDate, endDate, SimpleTrigger.REPEAT_INDEFINITELY, 60L * 1000L);
            scheduler.scheduleJob(jobDetail, trigger);
        }
    }
}
