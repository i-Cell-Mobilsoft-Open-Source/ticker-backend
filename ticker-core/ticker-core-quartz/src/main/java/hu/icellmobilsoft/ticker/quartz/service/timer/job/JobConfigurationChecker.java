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
package hu.icellmobilsoft.ticker.quartz.service.timer.job;

import java.text.MessageFormat;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;

import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.ticker.quartz.service.exception.InvalidConfigurationException;
import hu.icellmobilsoft.ticker.quartz.service.exception.QuartzException;
import hu.icellmobilsoft.ticker.quartz.service.timer.annotation.Timer;
import hu.icellmobilsoft.ticker.quartz.service.timer.config.ITimerConfig;
import hu.icellmobilsoft.ticker.quartz.service.timer.job.dummy.DummyJob;
import hu.icellmobilsoft.ticker.quartz.service.timer.job.httpclient.HttpClientJob;
import hu.icellmobilsoft.ticker.quartz.service.timer.job.httpclient.HttpClientJobConfig;
import hu.icellmobilsoft.ticker.quartz.service.timer.job.httpclient.HttpClientJobConfigChecker;
import hu.icellmobilsoft.ticker.quartz.service.timer.job.mprestclient.MicroprofileRestClientJob;
import hu.icellmobilsoft.ticker.quartz.service.timer.job.mprestclient.MicroprofileRestClientJobConfig;
import hu.icellmobilsoft.ticker.quartz.service.timer.job.mprestclient.MicroprofileRestClientJobConfigChecker;

/**
 * Timer job configuration checker
 *
 * @author tamas.cserhati
 * @since 1.6.0
 */
@ApplicationScoped
public class JobConfigurationChecker {

    /**
     * default constructor
     */
    public JobConfigurationChecker() {
    }

    /**
     * Quartz context store this key for job
     */
    public static final String CONFIG_KEY = "CONFIG_KEY";

    /**
     * Validate job configuration
     *
     * @param configKeys
     *            the available config keys
     */
    public void validateConfiguration(List<String> configKeys) {
        if (CollectionUtils.isEmpty(configKeys)) {
            throw new InvalidConfigurationException("Missing job configuration");
        }
        for (String configKey : configKeys) {
            check(configKey);
        }
    }

    /**
     * Check given job configuration
     *
     * @param configKey
     *            config key
     */
    public void check(String configKey) {
        if (StringUtils.isBlank(configKey)) {
            throw new InvalidConfigurationException("ConfigKey is empty");
        }

        Instance<ITimerConfig> timerConfigInstance = CDI.current().select(ITimerConfig.class, new Timer.Literal(configKey));
        ITimerConfig timerConfig = timerConfigInstance.get();

        try {
            Class<? extends Job> clazz = jobPrototypeInstance(timerConfig.actionClass());
            validateJobConfig(timerConfig, configKey);
        } catch (BaseException e) {
            throw new InvalidConfigurationException(e);
        } finally {
            timerConfigInstance.destroy(timerConfig);

        }
    }

    /**
     * job config validation via config key
     * 
     * @param timerConfig
     *            the timer config instance
     * @param configKey
     *            the configuration key
     */
    private void validateJobConfig(ITimerConfig timerConfig, String configKey) {
        if (StringUtils.equals(timerConfig.actionClass(), MicroprofileRestClientJob.class.getName())) {
            CDI.current()
                    .select(MicroprofileRestClientJobConfigChecker.class)
                    .get()
                    .check(timerConfig, configKey, MicroprofileRestClientJobConfig.class);
        } else if (StringUtils.equals(timerConfig.actionClass(), HttpClientJob.class.getName())) {
            CDI.current().select(HttpClientJobConfigChecker.class).get().check(timerConfig, configKey, HttpClientJobConfig.class);
        } else if (!StringUtils.equals(timerConfig.actionClass(), DummyJob.class.getName())) {
            throw new InvalidConfigurationException("Invalid action class: " + timerConfig.actionClass());
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Job> jobPrototypeInstance(String actionClassName) throws BaseException {
        try {
            Class<?> actionClass = ClassUtils.getClass(actionClassName);
            if (Job.class.isAssignableFrom(actionClass)) {
                return (Class<? extends Job>) actionClass;
            } else {
                throw new QuartzException(MessageFormat.format("The class [{0}] is not instance of org.quartz.Job class!", actionClass));
            }
        } catch (ClassNotFoundException e) {
            throw new QuartzException(MessageFormat.format("Invalid class in actionClass parameter: [{0}]!", e.getLocalizedMessage()), e);
        }
    }
}
