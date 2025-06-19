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
package hu.icellmobilsoft.test.ticker.core.quartz.service;

import java.util.List;

import jakarta.inject.Inject;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.ticker.quartz.service.quartz.health.TickerHealthCheckStatus;
import hu.icellmobilsoft.ticker.quartz.service.quartz.util.QuartzJobStatus;

/**
 * Abstract job configuration checker class
 * 
 * @author tamas.cserhati
 * @since 1.6.0
 */
public abstract class AbstractQuarkusTest {

    @Inject
    TickerHealthCheckStatus tickerHealthCheckStatus;

    /**
     * @return get the expected job keys
     */
    protected abstract List<String> getExpectedJobKeys();
    
    /**
     * Testing job configuration
     * 
     * @param expectedJobKeys
     *            expected job keys
     */
    @Test
    @DisplayName("Testing job configuration")
    public void testJobKeys() {
        List<QuartzJobStatus> quartzJobStatusList = tickerHealthCheckStatus.getSchedulerJobSummary();
        List<String> jobKeys = quartzJobStatusList.stream().map(QuartzJobStatus::getJobKey).sorted().toList();
        MatcherAssert.assertThat(jobKeys, CoreMatchers.is(CoreMatchers.equalTo(getExpectedJobKeys().stream().map(jobKey -> jobKey + "-Job").sorted().toList())));
    }

}
