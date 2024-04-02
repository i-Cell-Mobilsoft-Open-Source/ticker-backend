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
package hu.icellmobilsoft.ticker.quartz.service.timer.config;

import java.util.List;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;

import hu.icellmobilsoft.ticker.quartz.service.timer.job.dummy.DummyJob;

/**
 * Helper class for obtaining quartz settings using microprofile config.<br>
 * General pattern for activating jobs "{@code ticker.timer.activeJobs.${configKeys}}<br>
 * General pattern for job settings "{@code ticker.timer.job.${configKey}.${setting}}
 *
 * i.e.:
 *
 * <pre>
 *  ticker:
 *    timer:
 *      activeJobs:
 *        - _key_
 *      job:
 *        _key_:
 *          code: TEST
 *          cron: "0 0/5 * * * ? *"
 *          actionClass: hu.icellmobilsoft.quartz.job.action.Test
 * </pre>
 *
 * The upper configuration is injectable with:
 *
 * <pre>
 * &#64;Inject
 * &#64;Timer(configKey = "test")
 * ITimerConfig config;
 * </pre>
 *
 * or:
 *
 * <pre>
 * ITimerConfig config = CDI.current().select(ITimerConfig.class, new Timer.Literal("test")).get();
 * </pre>
 *
 * @author Imre Scheffer
 * @author mate.biro
 * @since 0.1.0
 */
@Dependent
public class TimerConfigImpl implements ITimerConfig {

    @Inject
    Config config;

    private String configKey;

    @Override
    public List<String> activeJobs() {
        return config.getOptionalValues(TIMER_ACTIVEJOBS, String.class).orElse(List.of("DUMMY"));
    }

    @Override
    public String code() {
        return config.getOptionalValue(joinKey(CONFIG_CODE), String.class).orElse("DUMMY");
    }

    @Override
    public String cron() {
        return config.getOptionalValue(joinKey(CONFIG_CRON), String.class).orElse("0 * * * * ? *");
    }

    @Override
    public String actionClass() {
        return config.getOptionalValue(joinKey(CONFIG_ACTIONCLASS), String.class).orElse(DummyJob.class.getName());
    }

    /**
     * Getter for the field {@code configKey}.
     *
     * @return configKey
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * Setter for the field {@code configKey}.
     *
     * @param configKey
     *            configKey to set
     */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    private String joinKey(String key) {
        return String.join(KEY_DELIMITER, TIMER_PREFIX, CONFIG_JOB, configKey, key);
    }
}
