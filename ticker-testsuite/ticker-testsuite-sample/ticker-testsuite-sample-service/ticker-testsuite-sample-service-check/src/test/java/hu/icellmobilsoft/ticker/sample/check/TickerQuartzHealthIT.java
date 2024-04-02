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
package hu.icellmobilsoft.ticker.sample.check;

import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.roaster.api.TestSuiteGroup;
import hu.icellmobilsoft.roaster.restassured.BaseConfigurableWeldIT;
import hu.icellmobilsoft.roaster.restassured.helper.HealthCheckTestHelper;
import hu.icellmobilsoft.ticker.testsuite.quartz.common.TickerQuartzITConfigurationConstants;

/**
 * Ticker Quartz mp health test
 *
 * @author mate.biro
 * @since 0.2.0
 */
@DisplayName("Testing Ticker Quartz mp-health")
@Tag(TestSuiteGroup.RESTASSURED)
class TickerQuartzHealthIT extends BaseConfigurableWeldIT {

    @Inject
    @ConfigProperty(name = TickerQuartzITConfigurationConstants.TICKER_SERVICE_QUARKUS_URI)
    private String tickerQuartzServiceQuarkusUri;

    @Inject
    private HealthCheckTestHelper healthCheckTestHelper;

    @Test
    @DisplayName("Testing Ticker Quartz /health")
    void testHealthCheck() {
        healthCheckTestHelper.testHealth(tickerQuartzServiceQuarkusUri);
    }
}
