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
            String jobName = jobE.getAttribute("name");
            JobDetail jobDetail = new JobDetail(jobName, groupName, Class.forName(jobE.getAttribute("class")));

            Element triggerElement = (Element) jobE.getElementsByTagName("trigger").item(0);

            Date startDate = new Date();
            String startDateA = triggerElement.getAttribute("startDate");
            if (startDateA != null && startDateA.length() > 0) {
                if (startDateA.equals("NOW")) {
                    startDate = new Date();
                } else {
                    try {
                        startDate = new java.text.SimpleDateFormat("yyyy.MM.dd'T'HH:mm:ss").parse(startDateA);
                    } catch(java.text.ParseException e) {
                        log.error("Could not parse startDate: " + e.getMessage() + " (Use NOW as start date)");
                    }
                }
            }

            Date endDate = null;
            String endDateA = triggerElement.getAttribute("endDate");
            if (endDateA != null && endDateA.length() > 0) {
                try {
                    endDate = new java.text.SimpleDateFormat("yyyy.MM.dd'T'HH:mm:ss").parse(endDateA);
                } catch(java.text.ParseException e) {
                    log.error("Could not parse endDate: " + e.getMessage() + " (No end date set)");
                }
            }

            // TODO: Implement repeat count and interval
            Trigger trigger = new SimpleTrigger(jobName + "Trigger", groupName, startDate, endDate, SimpleTrigger.REPEAT_INDEFINITELY, 60L * 1000L);
            scheduler.scheduleJob(jobDetail, trigger);
        }
    }
}
