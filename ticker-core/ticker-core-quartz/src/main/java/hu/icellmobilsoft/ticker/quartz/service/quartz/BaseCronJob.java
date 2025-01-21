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
package hu.icellmobilsoft.ticker.quartz.service.quartz;

import java.text.MessageFormat;
import java.util.Date;

import org.jboss.logging.MDC;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import hu.icellmobilsoft.coffee.cdi.trace.annotation.Traced;
import hu.icellmobilsoft.coffee.dto.common.LogConstants;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.se.util.string.RandomUtil;
import hu.icellmobilsoft.coffee.tool.utils.clazz.ResourceUtil;
import hu.icellmobilsoft.coffee.tool.utils.date.DatePrintUtil;
import hu.icellmobilsoft.coffee.tool.utils.date.DateUtil;

/**
 * Base cron job.
 *
 * @author imre.scheffer
 * @author mate.biro
 * @since 0.1.0
 */
public abstract class BaseCronJob implements Job {

    private static final Logger logger = Logger.getLogger(BaseCronJob.class);

    @Override
    @Traced(component = "quartz-job")
    public void execute(JobExecutionContext context) {
        String jobType = context.getJobDetail().getDescription();
        MDC.put(LogConstants.LOG_SERVICE_NAME, ResourceUtil.getAppName(getClass()));
        MDC.put(LogConstants.LOG_SESSION_ID, RandomUtil.generateId());

        if (logger.isInfoEnabled()) {
            logger.info(
                    ">>> Start quartz job type [{0}], fireTime: [{1}], nextFireTime: [{2}]",
                    jobType,
                    printDate(context.getFireTime()),
                    printDate(context.getNextFireTime()));
        }
        try {
            preExecuteJob(context);
            executeJob(context);
        } catch (BaseException e) {
            logger.error(MessageFormat.format("Handled exception in quartz job type [{0}]: [{1}]", jobType, e.getLocalizedMessage()), e);
        } catch (Exception e) {
            logger.error(MessageFormat.format("Unhandled exception in quartz job type [{0}]: [{1}]", jobType, e.getLocalizedMessage()), e);
        } finally {
            try {
                postExecuteJob(context);
            } catch (Exception e) {
                logger.error(MessageFormat.format("Exception in postExecuteJob quartz job type [{0}]: [{1}]", jobType, e.getLocalizedMessage()), e);
            }
            logger.info("<<< End quartz job type [{0}]", jobType);
            MDC.clear();
        }
    }

    private String printDate(Date date) {
        return DatePrintUtil.printDate(date, DateUtil.DEFAULT_FULL_PATTERN);
    }

    /**
     * Main method to define job business logic
     *
     * @param context
     *            Quartz context
     * @throws BaseException
     *             if any error occurs
     */
    public abstract void executeJob(JobExecutionContext context) throws BaseException;

    /**
     * Runned before "executeJob"
     *
     * @param context
     *            context
     */
    protected void preExecuteJob(JobExecutionContext context) {
    }

    /**
     * Runned after "executeJob". Called in finally statement
     *
     * @param context
     *            context
     */
    protected void postExecuteJob(JobExecutionContext context) {
    }
}
