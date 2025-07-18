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
import jakarta.inject.Inject;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.ticker.quartz.service.quartz.BaseCronJob;

/**
 * Test job for Quartz
 * 
 * @author mate.biro
 * @since 1.6.0
 */
@Dependent
@DisallowConcurrentExecution
public class TestJob extends BaseCronJob {

    @Inject
    @ThisLogger
    AppLogger log;

    /**
     * Execute the job
     *
     * @param context
     *            Quartz context
     */
    @ActivateRequestContext
    @Override
    public void executeJob(JobExecutionContext context) {
        log.info("execute TestJob");
    }
}
