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
package hu.icellmobilsoft.ticker.quartz.service.timer.job.dummy;

import jakarta.enterprise.context.Dependent;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.ticker.quartz.service.quartz.BaseCronJob;

/**
 * DummyJob for actionClass misconfiguration
 *
 * @author mate.biro
 * @since 0.1.0
 */
@Dependent
@DisallowConcurrentExecution
public class DummyJob extends BaseCronJob {

    @Override
    public void executeJob(JobExecutionContext context) {
        Logger.getLogger(DummyJob.class).warn("DummyJob executed! No actionClass configured in Timer");
    }
}
