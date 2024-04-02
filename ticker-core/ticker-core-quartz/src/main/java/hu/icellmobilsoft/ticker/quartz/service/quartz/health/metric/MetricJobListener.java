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
package hu.icellmobilsoft.ticker.quartz.service.quartz.health.metric;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Trigger;

import hu.icellmobilsoft.ticker.metrics.MetricsHelper;
import hu.icellmobilsoft.ticker.metrics.constants.MetricsConstants;

/**
 * Quartz jobok adatait metrikába helyezi
 *
 * @author czenczl
 * @author mate.biro
 * @since 0.2.0
 */
@ApplicationScoped
@Named
public class MetricJobListener implements JobListener {

    @Inject
    MetricsHelper metricsHelper;

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        // no notification
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        // no notification
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        Trigger trigger = context.getTrigger();

        // prev fire time
        metricsHelper.addHistorgramMetric(
                MetricsConstants.Quartz.Name.QUARTZ_JOB_PREV_FIRE_TIME,
                MetricsConstants.Quartz.Description.QUARTZ_JOB_PREV_FIRE_TIME_DESCRIPTION,
                trigger.getPreviousFireTime() != null ? trigger.getPreviousFireTime().toInstant().toEpochMilli() : 0,
                MetricsConstants.Tag.CONFIG_KEY,
                trigger.getJobKey().getName());

        // next fire time
        metricsHelper.addHistorgramMetric(
                MetricsConstants.Quartz.Name.QUARTZ_JOB_NEXT_FIRE_TIME,
                MetricsConstants.Quartz.Description.QUARTZ_JOB_NEXT_FIRE_TIME_DESCRIPTION,
                trigger.getNextFireTime() != null ? trigger.getNextFireTime().toInstant().toEpochMilli() : 0,
                MetricsConstants.Tag.CONFIG_KEY,
                trigger.getJobKey().getName());

        // run time
        metricsHelper.addHistorgramMetric(
                MetricsConstants.Quartz.Name.QUARTZ_JOB_RUN_TIME,
                MetricsConstants.Quartz.Description.QUARTZ_JOB_RUN_TIME_DESCRIPTION,
                context.getJobRunTime(),
                MetricsConstants.Tag.CONFIG_KEY,
                trigger.getJobKey().getName());
    }
}
