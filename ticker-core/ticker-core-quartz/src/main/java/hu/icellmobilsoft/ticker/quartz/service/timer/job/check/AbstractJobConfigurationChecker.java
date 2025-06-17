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
package hu.icellmobilsoft.ticker.quartz.service.timer.job.check;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;

import org.quartz.CronExpression;

import hu.icellmobilsoft.ticker.quartz.service.exception.InvalidConfigurationException;
import hu.icellmobilsoft.ticker.quartz.service.timer.config.ITimerConfig;
import hu.icellmobilsoft.ticker.quartz.service.timer.job.AbstractJobConfig;

/**
 * Abstract job configuration checker
 * 
 * @author tamas.cserhati
 *
 * @param <T>
 *            type of Job configuration
 */
public abstract class AbstractJobConfigurationChecker<T extends AbstractJobConfig> {

    /**
     * default constructor
     */
    public AbstractJobConfigurationChecker() {
    }

    /**
     * validate configuration
     * 
     * @param timerConfig
     *            the timer configuration
     * @param configKey
     *            the config key
     * @param configClass
     *            the configuration class
     */
    public void check(ITimerConfig timerConfig, String configKey, Class<T> configClass) {
        Instance<T> instance = null;
        T config = null;
        try {
            instance = CDI.current().select(configClass);
            config = instance.get();
            config.setConfigKey(configKey);
            validate(config, configKey);
            isCronExpressionValid(timerConfig.cron());
        } finally {
            if (instance != null && config != null) {
                instance.destroy(config);
            }
        }
    }

    /**
     * Validate the given type of configuration
     * 
     * @param config
     *            the configuration
     * @param configKey
     *            the configuration key
     */
    protected abstract void validate(T config, String configKey);

    /**
     * checks the class can be loaded
     * 
     * @param className
     *            the className to validate
     */
    protected static void isClassNameValid(String className) {
        try {
            Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new InvalidConfigurationException("Cannot load class: " + className, e);
        }
    }

    /**
     * checks the cron expression syntax
     * 
     * @param cronExpression
     *            the cron expression to validate
     */
    protected static void isCronExpressionValid(String cronExpression) {
        if (!CronExpression.isValidExpression(cronExpression)) {
            throw new InvalidConfigurationException("Invalid CRON expression: " + cronExpression);
        }
    }

}
