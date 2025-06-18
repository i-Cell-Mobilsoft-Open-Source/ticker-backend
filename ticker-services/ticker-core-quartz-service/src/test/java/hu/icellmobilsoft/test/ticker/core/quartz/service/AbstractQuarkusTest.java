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
    private TickerHealthCheckStatus tickerHealthCheckStatus;

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
        MatcherAssert.assertThat(jobKeys, CoreMatchers.is(CoreMatchers.equalTo(getExpectedJobKeys().stream().map(jobKey -> jobKey + "-Job").sorted())));
    }

}
