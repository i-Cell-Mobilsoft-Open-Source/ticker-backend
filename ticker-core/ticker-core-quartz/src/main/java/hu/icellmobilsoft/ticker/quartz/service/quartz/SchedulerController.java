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
package hu.icellmobilsoft.ticker.quartz.service.quartz;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.ticker.quartz.service.timer.config.ITimerConfig;
import hu.icellmobilsoft.ticker.quartz.service.timer.job.JobRegistrar;
import io.quarkus.runtime.StartupEvent;

/**
 * Programmatic scheduler controller.
 *
 * @author mate.biro
 * @since 0.1.0
 */
@ApplicationScoped
public class SchedulerController {

    @Inject
    @ThisLogger
    AppLogger log;

    @Inject
    ITimerConfig timerConfig;

    /**
     * Scheduling Jobs Programmatically.
     *
     * @param event
     *            {@link StartupEvent}
     */
    void onStart(@Observes StartupEvent event) {
        List<String> configKeys = timerConfig.activeJobs();

        try {
            for (String configKey : configKeys) {
                JobRegistrar.register(configKey);
            }
        } catch (BaseException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }
}
