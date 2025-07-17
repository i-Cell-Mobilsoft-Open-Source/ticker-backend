/*-
 * #%L
 * Ticker
 * %%
 * Copyright (C) 2024 - 2025 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.ticker.core.quartz.service.job;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.control.ActivateRequestContext;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.ticker.quartz.service.quartz.BaseCronJob;

/**
 * Job to process scheduled tasks. This job is executed by the Quartz scheduler to handle scheduled tasks. It extends the BaseCronJob class and
 * implements the executeJob method.
 *
 * @author mate.biro
 * @since 1.6.0
 */
@Dependent
@DisallowConcurrentExecution
public class ProcessScheduledTaskJob extends BaseCronJob {

    private final AppLogger log;

    /**
     * Constructor for ProcessScheduledTaskJob.
     *
     * @param log
     *            logger instance
     */
    public ProcessScheduledTaskJob(
            @ThisLogger AppLogger log
    ) {
        this.log = log;
    }

    /**
     * Execute the job to process scheduled tasks.
     *
     * @param context
     *            Quartz context
     */
    @ActivateRequestContext
    @Override
    public void executeJob(JobExecutionContext context) {
        log.info("execute ProcessScheduledTaskJob");
    }
}
