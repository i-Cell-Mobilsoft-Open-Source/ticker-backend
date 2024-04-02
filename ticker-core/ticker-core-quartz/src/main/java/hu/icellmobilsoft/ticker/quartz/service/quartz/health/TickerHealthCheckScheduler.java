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
package hu.icellmobilsoft.ticker.quartz.service.quartz.health;

import java.text.MessageFormat;
import java.util.List;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import hu.icellmobilsoft.coffee.tool.utils.date.DatePrintUtil;
import hu.icellmobilsoft.coffee.tool.utils.date.DateUtil;
import hu.icellmobilsoft.ticker.common.health.AbstractBaseHealthCheck;
import hu.icellmobilsoft.ticker.common.health.IHealth;
import hu.icellmobilsoft.ticker.quartz.service.quartz.util.QuartzJobStatus;

/**
 * Health check interface extension
 * 
 * @author arnold.bucher
 * @author czenczl
 * @author mate.biro
 * @since 0.2.0
 */
@Dependent
public class TickerHealthCheckScheduler extends AbstractBaseHealthCheck implements IHealth {

    /**
     * Ticker Quartz constant
     */
    public static final String TICKER_QUARTZ = "ticker-quartz";

    /**
     * Ticker health check status
     */
    @Inject
    TickerHealthCheckStatus tickerHealthCheckStatus;

    /**
     * Scheduler job status
     * 
     * @return health check response
     */
    @Override
    public HealthCheckResponse checkReadiness() {
        HealthCheckResponseBuilder builder = createHealthCheckResponseBuilder().up();

        List<QuartzJobStatus> schedulerJobSummary = tickerHealthCheckStatus.getSchedulerJobSummary();
        for (QuartzJobStatus quartzJobStatus : schedulerJobSummary) {
            if (quartzJobStatus == null) {
                continue;
            }

            String previousFireTime = DatePrintUtil.printDate(quartzJobStatus.getPreviousFireTime(), DateUtil.DEFAULT_FULL_PATTERN);
            String nextFireTime = DatePrintUtil.printDate(quartzJobStatus.getNextFireTime(), DateUtil.DEFAULT_FULL_PATTERN);
            builder.withData(
                    quartzJobStatus.getJobKey(),
                    MessageFormat.format("PreviousFireTime [{0}], NextFireTime [{1}]", previousFireTime, nextFireTime));
        }
        return builder.build();
    }

    /**
     * producer for ticker readiness check
     *
     * @return health check functional interface
     */
    @Produces
    @Readiness
    public HealthCheck produceTickerCheck() {
        return this::checkReadiness;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBuilderName() {
        return TICKER_QUARTZ;
    }
}
