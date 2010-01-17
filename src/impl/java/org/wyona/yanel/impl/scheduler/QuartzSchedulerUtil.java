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

import org.wyona.yanel.core.map.Realm;

/**
 *
 */
public class QuartzSchedulerUtil {

    private static Logger log = Logger.getLogger(QuartzSchedulerUtil.class);

    /**
     * Schedule jobs based on XML configuration
     * @param scheduler Scheduler
     * @param doc XML document containing jobs configuration
     * @param realm Realm
     */
    public static void schedule(Scheduler scheduler, Document doc, Realm realm) throws Exception {
        String groupName = realm.getID();
        log.info("Add jobs for group/realm '" + groupName + "' to scheduler.");

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
            jobDetail.getJobDataMap().put("realm", realm);

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
            Element repeatElement = (Element) triggerElement.getElementsByTagName("repeat").item(0);

            int count = SimpleTrigger.REPEAT_INDEFINITELY;
            long interval = 60000; // INFO: 60 seconds
            if (repeatElement != null) {
                String countA = repeatElement.getAttribute("count");
                if (countA.equals("REPEAT_INDEFINITELY")) {
                    count = SimpleTrigger.REPEAT_INDEFINITELY;
                } else {
                    try {
                        count = Integer.parseInt(countA);
                    } catch(NumberFormatException e) {
                        log.error("Could not parse count: " + e.getMessage() + " (repeat indefinitely)");
                        count = SimpleTrigger.REPEAT_INDEFINITELY;
                    }
                }

                String intervalA = repeatElement.getAttribute("interval");
                try {
                    interval = Long.parseLong(intervalA);
                } catch(NumberFormatException e) {
                    log.error("Could not parse interval: " + e.getMessage() + " (60 seconds)");
                    interval = 60000;
                }
            }

            Trigger trigger = new SimpleTrigger(jobName + "Trigger", groupName, startDate, endDate, count, interval);
            scheduler.scheduleJob(jobDetail, trigger);
        }
    }
}
