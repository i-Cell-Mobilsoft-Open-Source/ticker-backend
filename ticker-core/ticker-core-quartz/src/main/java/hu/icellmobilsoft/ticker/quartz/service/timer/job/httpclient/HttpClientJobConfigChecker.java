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
package hu.icellmobilsoft.ticker.quartz.service.timer.job.httpclient;

import jakarta.enterprise.context.ApplicationScoped;

import hu.icellmobilsoft.coffee.cdi.logger.LogProducer;
import hu.icellmobilsoft.ticker.quartz.service.timer.job.JobConfigurationChecker;
import hu.icellmobilsoft.ticker.quartz.service.timer.job.check.AbstractJobConfigurationChecker;
import io.quarkus.arc.Unremovable;

/**
 * Configuration checker for {@link HttpClientJob}
 *
 * @author tamas.cserhati
 * @since 1.6.0
 */
@ApplicationScoped
@Unremovable
public class HttpClientJobConfigChecker extends AbstractJobConfigurationChecker<HttpClientJobConfig> {

    @Override
    protected void validate(HttpClientJobConfig config, String configKey) {
        LogProducer.logToAppLogger(
                log -> log.info("HTTP Client job is not validated on startup: [{0}]", config.getConfigKey()),
                JobConfigurationChecker.class);
    }
}
