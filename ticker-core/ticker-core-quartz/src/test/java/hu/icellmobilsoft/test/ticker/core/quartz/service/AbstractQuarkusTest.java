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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.inject.Inject;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
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
     *
     * This method should be implemented by subclasses to provide the expected job keys. <br>
     * <ul>
     * <li>The expected job keys are the <code>ticker.timer.job.[jobName].code</code> configurations.</li>
     * <li>The expected jobs should be active as well, thus listed in <code>ticker.timer.activeJobs</code> configuration.</li>
     * </ul>
     *
     *
     * @return the expected job keys
     */
    protected abstract List<String> getExpectedJobKeys();

    /**
     * Testing job configuration
     *
     */
    @Test
    @DisplayName("Testing job configuration")
    public void testJobKeys() {
        List<QuartzJobStatus> quartzJobStatusList = tickerHealthCheckStatus.getSchedulerJobSummary();

        List<String> jobKeys = quartzJobStatusList.stream().map(QuartzJobStatus::getJobKey).sorted().toList();
        List<String> expectedJobKeys = getExpectedJobKeys().stream().map(jobKey -> jobKey + "-Job").sorted().toList();

        List<String> jobKeyDifferences = getJobDifferences(jobKeys, expectedJobKeys);
        List<String> expectedJobKeyDifferences = getJobDifferences(expectedJobKeys, jobKeys);

        List<AssertionError> errors = new ArrayList<>();

        softAssertAndCollectErrors("Configured jobs do not match the expected ones!", jobKeyDifferences, errors);
        softAssertAndCollectErrors("Expected jobs do not match the configured ones!", expectedJobKeyDifferences, errors);

        if (!errors.isEmpty()) {
            String combinedError = errors.stream().map(Throwable::getMessage).collect(Collectors.joining("\n"));
            throw new AssertionError(combinedError);
        }
    }

    private List<String> getJobDifferences(List<String> keys, List<String> keysToRemove) {
        List<String> keyDifferences = new ArrayList<>(keys);
        keyDifferences.removeAll(keysToRemove);
        return keyDifferences;
    }

    private void softAssertAndCollectErrors(String reason, List<String> differences, List<AssertionError> errors) {
        try {
            MatcherAssert.assertThat(reason, differences, Matchers.empty());
        } catch (AssertionError e) {
            errors.add(e);
        }
    }
}
