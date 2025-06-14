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

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.List;

import javax.management.RuntimeErrorException;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.quartz.Job;

import hu.icellmobilsoft.coffee.cdi.logger.LogProducer;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.api.exception.TechnicalException;
import hu.icellmobilsoft.ticker.common.dto.exception.enums.FaultType;
import hu.icellmobilsoft.ticker.quartz.service.exception.QuartzException;
import hu.icellmobilsoft.ticker.quartz.service.timer.annotation.Timer;
import hu.icellmobilsoft.ticker.quartz.service.timer.config.ITimerConfig;
import hu.icellmobilsoft.ticker.quartz.service.timer.job.dummy.DummyJob;
import hu.icellmobilsoft.ticker.quartz.service.timer.job.httpclient.HttpClientJob;
import hu.icellmobilsoft.ticker.quartz.service.timer.job.httpclient.HttpClientJobConfig;
import hu.icellmobilsoft.ticker.quartz.service.timer.job.mprestclient.MicroprofileRestClientJob;
import hu.icellmobilsoft.ticker.quartz.service.timer.job.mprestclient.MicroprofileRestClientJobConfig;

/**
 * Timer job configuration checker
 *
 * @author tamas.cserhati
 * @since 1.6.0
 */
public class JobChecker {

    private JobChecker() {
        // private constructor to hide the implicit public one
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
     * @throws BaseException
     *             if configuration has error
     */
    public static void validateConfiguration(List<String> configKeys) throws BaseException {
        if (CollectionUtils.isEmpty(configKeys)) {
            throw new TechnicalException(CoffeeFaultType.INVALID_INPUT, "Missing job configuration");
        }
        for (String configKey : configKeys) {
            JobChecker.check(configKey);
        }
    }

    /**
     * Check given job configuration
     *
     * @param configKey
     *            config key
     * @throws BaseException
     *             if any error occurs
     */
    public static void check(String configKey) throws BaseException {
        if (StringUtils.isBlank(configKey)) {
            throw new TechnicalException(CoffeeFaultType.INVALID_INPUT, "configKey is null!");
        }

        Instance<ITimerConfig> timerConfigInstance = CDI.current().select(ITimerConfig.class, new Timer.Literal(configKey));
        ITimerConfig timerConfig = timerConfigInstance.get();

        try {
            validateJobConfig(timerConfig.actionClass(), configKey);
            isCronExpressionValid(timerConfig.cron());
        } catch (BaseException e) {
            LogProducer.logToAppLogger(log -> log.error("job configuration error: [{0}]", e.getLocalizedMessage()), JobChecker.class);
            throw new RuntimeErrorException(new Error("Job configuration error: see logs for details!", e));
        } finally {
            timerConfigInstance.destroy(timerConfig);

        }
    }

    /**
     * job config validation via config key
     * 
     * @param actionClass
     *            the action class to check
     * @param configKey
     *            the configuration key
     * @throws BaseException
     *             if job config is invalid
     */
    private static void validateJobConfig(String actionClass, String configKey) throws BaseException {
        Class<? extends Job> clazz = jobPrototypeInstance(actionClass);

        if (StringUtils.equals(actionClass, MicroprofileRestClientJob.class.getName())) {
            Instance<MicroprofileRestClientJobConfig> instance = null;
            MicroprofileRestClientJobConfig config = null;
            try {
                instance = CDI.current().select(MicroprofileRestClientJobConfig.class);
                config = instance.get();
                config.setConfigKey(configKey);
                validateMicroprofileRestClientJob(config);
            } finally {
                if (instance != null && config != null) {
                    instance.destroy(config);
                }
            }
        } else if (StringUtils.equals(actionClass, HttpClientJob.class.getName())) {
            Instance<HttpClientJobConfig> instance = CDI.current().select(HttpClientJobConfig.class);
            HttpClientJobConfig config = null;
            try {
                instance = CDI.current().select(HttpClientJobConfig.class);
                config = instance.get();
                config.setConfigKey(configKey);
                validateHttpClientJob(config);
            } finally {
                if (instance != null && config != null) {
                    instance.destroy(config);
                }
            }
        } else if (StringUtils.equals(actionClass, DummyJob.class.getName())) {
            // do nothing
        } else {
            throw new TechnicalException(FaultType.VALIDATION_ERROR, "Invalid action class: " + actionClass);
        }
    }

    private static void validateMicroprofileRestClientJob(MicroprofileRestClientJobConfig config) throws BaseException {
        LogProducer.logToAppLogger(log -> log.info("Validating job [{0}]", config.getConfigKey()), JobChecker.class);
        isClassNameValid(config.mpRestClientClass());
        isMethodCallable(config);
    }

    private static void validateHttpClientJob(HttpClientJobConfig config) throws BaseException {
        LogProducer.logToAppLogger(log -> log.info("HTTP Client job is not validated on startup: [{0}]", config.getConfigKey()), JobChecker.class);
    }

    /**
     * checks the class can be loaded
     * 
     * @param className
     *            the className to validate
     * @throws BaseException
     *             if class name cannot be loaded
     */
    private static void isClassNameValid(String className) throws BaseException {
        try {
            Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new TechnicalException(FaultType.VALIDATION_ERROR, "Cannot load class: " + className);
        }
    }

    /**
     * cheks the method can be called
     * 
     * @param jobConfig
     * @throws BaseException
     *             if method configuration has error(s)
     */
    private static void isMethodCallable(MicroprofileRestClientJobConfig jobConfig) throws BaseException {
        String rawMethodName = null;
        try {
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(jobConfig.mpRestClientClass());
            rawMethodName = jobConfig.method();

            // "getTest(java.lang.String,java.lang.Integer)"
            int parenIndex = rawMethodName.indexOf('(');
            String methodName = rawMethodName.substring(0, parenIndex);
            String paramList = rawMethodName.substring(parenIndex + 1, rawMethodName.length() - 1); // "java.lang.String,java.lang.Integer"

            String[] paramTypeNames = paramList.isBlank() ? new String[0] : paramList.split("\\s*,\\s*");
            Class<?>[] parameterTypes = new Class<?>[paramTypeNames.length];

            for (int i = 0; i < paramTypeNames.length; i++) {
                parameterTypes[i] = Thread.currentThread().getContextClassLoader().loadClass(paramTypeNames[i]);
            }

            Method method = clazz.getMethod(methodName, parameterTypes);

        } catch (Exception e) {
            throw new TechnicalException(FaultType.VALIDATION_ERROR, "Check method invocation failed: " + rawMethodName, e);
        }
    }

    /**
     * checks the cron expression syntax
     * 
     * @param cronExpression
     *            the cron expression to validate
     * @throws BaseException
     *             if cron expression is invalid
     */
    private static void isCronExpressionValid(String cronExpression) throws BaseException {
        if (!CronExpression.isValidExpression(cronExpression)) {
            throw new TechnicalException(FaultType.VALIDATION_ERROR, "Invalid cron expression: " + cronExpression);
        }
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
