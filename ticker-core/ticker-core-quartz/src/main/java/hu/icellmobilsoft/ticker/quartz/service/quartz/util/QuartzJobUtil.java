/*-
 * #%L
 * Ticker
 * %%
 * Copyright (C) 2024 i-Cell Mobilsoft Zrt.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package hu.icellmobilsoft.ticker.quartz.service.quartz.util;

import java.text.MessageFormat;
import java.util.Date;

import jakarta.enterprise.inject.spi.CDI;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DateBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.ticker.quartz.service.exception.QuartzException;
import hu.icellmobilsoft.ticker.quartz.service.quartz.health.metric.MetricJobListener;

/**
 * Quartz utils
 *
 * @author imre.scheffer
 * @author mate.biro
 * @since 0.1.0
 */
public class QuartzJobUtil {

    private static final Logger logger = Logger.getLogger(QuartzJobUtil.class);

    private static final int PRIORITY = 8;
    private static final String GROUP = "quartz-scheduler";

    private QuartzJobUtil() {

    }

    /**
     * Registers given classes into Quartz
     *
     * @param scheduler
     *            Quartz context
     * @param jobClass
     *            Class to register
     * @param jobIdentifier
     *            Job identifier for internal keys ({@code TriggerKey}, {@code JobKey})
     * @param cron
     *            Cron expression
     * @param jobDataMap
     *            Job extra data, will be available in the context of the job execution
     * @throws QuartzException
     *             Initialization error
     */
    public static void registerCronJob(Scheduler scheduler, Class<? extends Job> jobClass, String jobIdentifier, String cron, JobDataMap jobDataMap)
            throws QuartzException {
        if (scheduler == null || jobClass == null || StringUtils.isAnyBlank(jobIdentifier, cron)) {
            throw new QuartzException("One or more input parameters is null!");
        }

        logger.info(">> Quartz job [{0}] scheduling.", jobIdentifier);

        TriggerKey triggerKey = triggerKey(jobIdentifier);
        JobKey jobKey = jobKey(jobIdentifier);

        ScheduleBuilder<CronTrigger> scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);

        try {
            // retrieve the trigger
            Trigger oldTrigger = scheduler.getTrigger(triggerKey);
            if (oldTrigger == null) {
                // Trigger the job to run on the next round minute
                Date runTime = DateBuilder.evenMinuteDateAfterNow();
                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(scheduleBuilder)
                        .startAt(runTime)
                        .withPriority(PRIORITY)
                        .withDescription(triggerDescription(jobIdentifier))
                        .build();

                // Define job instance
                // NOTE: it's not needed to get the unproxied jobClass because it is not a proxy class
                JobBuilder builder = JobBuilder.newJob(jobClass).withIdentity(jobKey).withDescription(jobDescription(jobIdentifier));
                if (jobDataMap != null) {
                    builder.usingJobData(jobDataMap);
                }
                JobDetail job = builder.build();

                // Schedule the job with the trigger
                scheduler.scheduleJob(job, trigger);
                logger.info("<< Quartz job [{0}] scheduled.", jobIdentifier);
            } else {
                logger.info("Found triggerKey [{0}].", oldTrigger.getDescription());

                // obtain a builder that would produce the trigger
                @SuppressWarnings("unchecked")
                TriggerBuilder<CronTrigger> tb = (TriggerBuilder<CronTrigger>) oldTrigger.getTriggerBuilder();
                // update the schedule associated with the builder, and build
                // the new trigger (other builder methods could be called, to
                // change the trigger in any desired way)
                CronTrigger newTrigger = tb.withSchedule(scheduleBuilder).withPriority(PRIORITY).build();
                scheduler.rescheduleJob(triggerKey, newTrigger);
                logger.info("<< Quartz job [{0}] rescheduled.", jobIdentifier);
            }

            // register job listener for metric
            MetricJobListener jobListener = CDI.current().select(MetricJobListener.class).get();
            scheduler.getListenerManager().addJobListener(jobListener);

        } catch (SchedulerException e) {
            String msg = MessageFormat.format("Error occurred when scheduling job: [{0}]", jobIdentifier);
            logger.error(msg, e);
            throw new QuartzException(msg, e);
        }
    }

    private static TriggerKey triggerKey(String jobType) {
        return new TriggerKey(jobType + "-Trigger", GROUP);
    }

    private static JobKey jobKey(String jobType) {
        return new JobKey(jobType + "-Job", GROUP);
    }

    private static ScheduleBuilder<CronTrigger> scheduleBuilder(String cron) {
        return CronScheduleBuilder.cronSchedule(cron);
    }

    private static String triggerDescription(String jobType) {
        return jobType + " trigger";
    }

    private static String jobDescription(String jobType) {
        return jobType + " job";
    }
}
