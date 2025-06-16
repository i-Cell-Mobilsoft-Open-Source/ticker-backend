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
package hu.icellmobilsoft.ticker.quartz.service.timer.job.mprestclient;

import java.lang.reflect.Method;

import jakarta.enterprise.context.ApplicationScoped;

import hu.icellmobilsoft.coffee.cdi.logger.LogProducer;
import hu.icellmobilsoft.ticker.quartz.service.exception.InvalidConfigurationException;
import hu.icellmobilsoft.ticker.quartz.service.timer.job.JobConfigurationChecker;
import hu.icellmobilsoft.ticker.quartz.service.timer.job.check.AbstractJobConfigurationChecker;
import io.quarkus.arc.Unremovable;

/**
 * Configuration checker for {@link MicroprofileRestClientJob}
 *
 * @author tamas.cserhati
 * @since 1.6.0
 */
@ApplicationScoped
@Unremovable
public class MicroprofileRestClientJobConfigChecker extends AbstractJobConfigurationChecker<MicroprofileRestClientJobConfig> {

    @Override
    protected void validate(MicroprofileRestClientJobConfig config, String configKey) {
        LogProducer.logToAppLogger(log -> log.info("Validating job [{0}]", config.getConfigKey()), JobConfigurationChecker.class);
        isClassNameValid(config.mpRestClientClass());
        isMethodCallable(config);
    }

    /**
     * checks the method can be called
     * 
     * @param jobConfig
     *            the MP job configuration
     */
    private static void isMethodCallable(MicroprofileRestClientJobConfig jobConfig) {
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
            throw new InvalidConfigurationException("Check method invocation failed: " + rawMethodName, e);
        }
    }

}
