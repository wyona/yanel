package org.wyona.yanel.impl.scheduler;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
//import org.quartz.impl.JobDetailImpl;

import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.wyona.yanel.core.map.Realm;

/**
 * Utility class to schedule jobs per realm
 */
public class QuartzSchedulerUtil {

    private static Logger log = LogManager.getLogger(QuartzSchedulerUtil.class);

    /**
     * Schedule jobs based on XML configuration
     * @param scheduler Scheduler instance
     * @param doc XML document containing job configurations
     * @param realm Realm which job(s) is(are) associated with
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
            String jobName = jobE.getAttribute("name"); // TODO: Maybe it's better to set the name automatically!

            String jobDesc = jobE.getAttribute("description");
            if (jobDesc != null && jobDesc.length() > 0) {
                // INFO: Leave job description as it is.
            } else {
                jobDesc = jobName;
            }

            Class<? extends org.quartz.Job> jobClass;
            try {
                jobClass = Class.forName(jobE.getAttribute("class")).asSubclass(org.quartz.Job.class);
            } catch(java.lang.ClassNotFoundException e) {
                log.error("Job class '" + jobE.getAttribute("class") + "' not found, which has been scheduled/configured within realm '" + realm.getID() + "'");
                log.error(e, e);
                continue;
            }
            //JobDetailImpl jobDetail = new org.quartz.impl.JobDetailImpl(jobName, groupName, jobClass);
            //jobDetail.setDescription(jobDesc);
            JobDetail jobDetail = org.quartz.JobBuilder.newJob(jobClass).withIdentity(jobName, groupName).withDescription(jobDesc).build();
            jobDetail.getJobDataMap().put("realm", realm);
            jobDetail.getJobDataMap().put("job-config", doc);

            Element triggerElement = (Element) jobE.getElementsByTagName("trigger").item(0);

            Date currentDate = new Date();

            Date startDate = new Date();
            boolean startDateOlderThanCurrentDate = false;
            String startDateA = triggerElement.getAttribute("startDate");
            if (startDateA != null && startDateA.length() > 0) {
                if (startDateA.equals("NOW")) {
                    startDate = new Date(); // TODO: What about a margin to make sure that startDate is not older than current date once trigger is actually checking start date
                    log.debug("Use current date '" + startDate + "' as start date.");
                } else {
                    try {
                        startDate = new java.text.SimpleDateFormat("yyyy.MM.dd'T'HH:mm:ss").parse(startDateA);
                        if (startDate.before(currentDate)) { // INFO: Also see http://www.javacodegeeks.com/2012/04/quartz-scheduler-misfire-instructions.html
                            log.warn("Configured start date '" + startDate + "' (Realm: " + realm.getID() + ") is older than current date '" + currentDate + "'!");
                            startDateOlderThanCurrentDate = true;
                        } else {
                            log.debug("Configured start date '" + startDate + "' is younger than current date '" + currentDate + "'!");
                        }
                    } catch(java.text.ParseException e) {
                        log.error("Could not parse startDate: " + e.getMessage() + " (Use current date '" + startDate + "' as start date)");
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

            int count = 1;
            long interval = 60000; // INFO: 60 seconds
            if (repeatElement != null) {
                String countA = repeatElement.getAttribute("count");
                if (countA.equals("REPEAT_INDEFINITELY")) {
                    count = SimpleTrigger.REPEAT_INDEFINITELY;
                } else {
                    try {
                        count = Integer.parseInt(countA);
                    } catch(NumberFormatException e) {
                        log.error("Could not parse count: " + e.getMessage() + " (hence set counter to zero)");
                        count = 0;
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

            if (startDateOlderThanCurrentDate && count == 1) {
                log.warn("Configured start date '" + startDate + "' is older than current date '" + currentDate + "' and the count is one, hence do not trigger this job (Realm: " + realm.getID() + ")!");
                return;
            }
            if (count == 0) {
                log.warn("Count is zero, hence do not trigger this job (Realm: " + realm.getID() + ")!");
                return;
            }
            if (startDateOlderThanCurrentDate && count > 1) {
                startDate = getNextPossibleStartDate(startDate, count, interval, currentDate);
                if (startDate.before(currentDate)) {
                    log.warn("Next possible start date '" + startDate + "' is also older than current date '" + currentDate + "', hence do not trigger this job (Realm: " + realm.getID() + ")!");
                    return;
                }
            }

            String triggerKey = jobName + "Trigger";
            Trigger trigger = new org.quartz.impl.triggers.SimpleTriggerImpl(triggerKey, groupName, startDate, endDate, count, interval);
            Date nextFireTime = scheduler.scheduleJob(jobDetail, trigger);

            if (startDateOlderThanCurrentDate && count == SimpleTrigger.REPEAT_INDEFINITELY) {
                log.warn("Configured start date '" + startDate + "' is older than current date '" + currentDate + "', but counter is set to 'REPEAT_INDEFINITELY', hence trigger this job (Realm: '" + realm.getID() + "', Job Description: '" + jobDesc+ "') for the first time at '" + trigger.getFireTimeAfter(currentDate) + "'!");
            }

        }
    }

    /**
     * Calculate next possible start date
     * @param startDate Original start date (which is supposed to be older than current date)
     * @param count How many times the interval will be applied
     * @param interval Time in between
     * @param currentDate Current date
     */
    private static Date getNextPossibleStartDate(Date startDate, int count, long interval, Date currentDate) {
        Date nextStartDate = startDate;
        for (int i = 0; i < count; i++) {
            nextStartDate = new Date(nextStartDate.getTime() + count*interval);
            log.debug("Next start date: " + nextStartDate);
            if (nextStartDate.after(currentDate)) {
                return nextStartDate;
            }
        }
        return nextStartDate;
    }
}
