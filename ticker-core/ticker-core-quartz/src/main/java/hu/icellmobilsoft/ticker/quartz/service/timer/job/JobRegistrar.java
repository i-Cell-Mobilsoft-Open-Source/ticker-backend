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

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.ticker.quartz.service.exception.QuartzException;
import hu.icellmobilsoft.ticker.quartz.service.quartz.util.QuartzJobUtil;
import hu.icellmobilsoft.ticker.quartz.service.timer.annotation.Timer;
import hu.icellmobilsoft.ticker.quartz.service.timer.config.ITimerConfig;

/**
 * Timer job registrar to quartz system
 *
 * @author mate.biro
 * @since 0.1.0
 */
public class JobRegistrar {

    private JobRegistrar() {
        // private constructor to hide the implicit public one
    }

    /**
     * Quartz context store this key for job
     */
    public static final String CONFIG_KEY = "CONFIG_KEY";

    /**
     * Register job to Quartz system
     *
     * @param configKey
     *            config key
     * @throws BaseException
     *             on error
     */
    public static void register(String configKey) throws BaseException {
        if (StringUtils.isBlank(configKey)) {
            throw new TechnicalException(CoffeeFaultType.INVALID_INPUT, "configKey is null!");
        }

        Scheduler quartzScheduler = CDI.current().select(Scheduler.class).get();

        Instance<ITimerConfig> timerConfigInstance = CDI.current().select(ITimerConfig.class, new Timer.Literal(configKey));
        ITimerConfig timerConfig = timerConfigInstance.get();
        Class<? extends Job> clazz = jobPrototypeInstance(timerConfig.actionClass());
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(CONFIG_KEY, configKey);

        String code = timerConfig.code();
        String cron = timerConfig.cron();

        QuartzJobUtil.registerCronJob(quartzScheduler, clazz, code, cron, jobDataMap);

        timerConfigInstance.destroy(timerConfig);
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Job> jobPrototypeInstance(String actionClassName) throws BaseException {
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
