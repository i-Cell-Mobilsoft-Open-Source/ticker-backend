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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;

import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.api.exception.TechnicalException;
import hu.icellmobilsoft.ticker.quartz.service.quartz.BaseCronJob;
import hu.icellmobilsoft.ticker.quartz.service.timer.job.JobRegistrar;

/**
 * Microprofile Rest Client Job
 *
 * @author Imre Scheffer
 * @author mate.biro
 * @since 0.1.0
 */
@Dependent
@DisallowConcurrentExecution
public class MicroprofileRestClientJob extends BaseCronJob {

    private static final String METHOD_CALL_PREFIX = "{";
    private static final String METHOD_CALL_SUFFIX = "}";

    @Inject
    MicroprofileRestClientJobConfig microprofileRestClientJobConfig;

    /** {@inheritDoc} */
    @ActivateRequestContext
    @Override
    public void executeJob(JobExecutionContext context) throws BaseException {
        microprofileRestClientJobConfig.setConfigKey(context.getJobDetail().getJobDataMap().getString(JobRegistrar.CONFIG_KEY));

        Class<?> restClientClass = restClientClass();
        Class<?>[] parameterTypes = methodParameterClasses();
        Method method = method(restClientClass, parameterTypes);
        Object[] methodParameters = methodParameters(parameterTypes);
        @SuppressWarnings("rawtypes")
        Instance restClientInstance = CDI.current().select(restClientClass, RestClient.LITERAL);
        Object restClient = restClientInstance.get();

        try {
            method.invoke(restClient, methodParameters);
        } catch (InvocationTargetException e) {
            throw new TechnicalException(
                    CoffeeFaultType.GENERIC_EXCEPTION,
                    MessageFormat.format("Error on invoke method [{0}]: [{1}]", method, e.getTargetException().getLocalizedMessage()),
                    e);
        } catch (Exception e) {
            throw new TechnicalException(
                    CoffeeFaultType.GENERIC_EXCEPTION,
                    MessageFormat.format("Error on invoke method [{0}]: [{1}]", method, e.getLocalizedMessage()),
                    e);
        }
    }

    private Class<?> restClientClass() throws BaseException {
        try {
            return ClassUtils.getClass(microprofileRestClientJobConfig.mpRestClientClass());
        } catch (ClassNotFoundException e) {
            throw new TechnicalException(
                    CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS,
                    MessageFormat.format(
                            "The [{0}]: [{1}] config is invalid: [{2}]",
                            microprofileRestClientJobConfig.getKey(MicroprofileRestClientJobConfig.CONFIG_KEY_MP_REST_CLIENT_CLASS),
                            microprofileRestClientJobConfig.mpRestClientClass(),
                            e.getLocalizedMessage()),
                    e);
        }
    }

    private Class<?>[] methodParameterClasses() throws BaseException {
        var parametersSubstring = StringUtils.substringBetween(microprofileRestClientJobConfig.method(), "(", ")");
        String[] parameterStrings = StringUtils.split(parametersSubstring, ',');
        List<Class<?>> list = new ArrayList<>();
        for (String parameterString : parameterStrings) {
            try {
                list.add(ClassUtils.getClass(parameterString));
            } catch (ClassNotFoundException e) {
                throw new TechnicalException(
                        CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS,
                        MessageFormat.format(
                                "Parameter [{0}] from config [{1}]: [{2}] is invalid: [{3}]",
                                parameterString,
                                microprofileRestClientJobConfig.getKey(MicroprofileRestClientJobConfig.CONFIG_KEY_METHOD),
                                microprofileRestClientJobConfig.method(),
                                e.getLocalizedMessage()),
                        e);
            }
        }
        return list.toArray(new Class[0]);
    }

    private Method method(Class<?> klass, Class<?>[] parameterTypes) throws BaseException {
        String methodName = StringUtils.substringBefore(microprofileRestClientJobConfig.method(), "(");
        try {
            return klass.getMethod(methodName, parameterTypes);
        } catch (Exception e) {
            throw new TechnicalException(
                    CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS,
                    MessageFormat.format(
                            "Error on getting java method by config [{0}]: [{1}]: [{2}]",
                            microprofileRestClientJobConfig.getKey(MicroprofileRestClientJobConfig.CONFIG_KEY_METHOD),
                            microprofileRestClientJobConfig.method(),
                            e.getLocalizedMessage()),
                    e);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object[] methodParameters(Class<?>[] parameterTypes) throws BaseException {
        List<String> parameters = microprofileRestClientJobConfig.parameters();
        if (parameters.size() != parameterTypes.length) {
            throw new TechnicalException(
                    CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS,
                    MessageFormat.format(
                            "Parameters size [{0}] and method parameters [{1}] of config [{2}] is not equal!",
                            parameters.size(),
                            parameterTypes.length,
                            microprofileRestClientJobConfig.getKey(MicroprofileRestClientJobConfig.CONFIG_KEY_PARAMETERS)));
        }
        Object[] result = new Object[parameters.size()];
        int i = 0;
        for (String parameter : parameters) {
            try {
                Object value;
                // this is a trick until https://github.com/smallrye/smallrye-config/issues/611 is fixed
                if (StringUtils.equalsIgnoreCase("null", parameter)) {
                    value = null;
                } else if (Integer.class.isAssignableFrom(parameterTypes[i])) {
                    value = Integer.valueOf(parameter);
                } else if (Long.class.isAssignableFrom(parameterTypes[i])) {
                    value = Long.valueOf(parameter);
                } else if (Boolean.class.isAssignableFrom(parameterTypes[i])) {
                    value = Boolean.valueOf(parameter);
                } else if (Enum.class.isAssignableFrom(parameterTypes[i])) {
                    value = EnumUtils.getEnum((Class<Enum>) parameterTypes[i], parameter);
                } else if (OffsetDateTime.class.isAssignableFrom(parameterTypes[i])) {
                    value = OffsetDateTime.parse(parameter);
                } else if (isMethodCall(parameter)) {
                    value = resolveMethod(parameter, parameterTypes[i]);
                } else {
                    value = parameterTypes[i].cast(parameter);
                }
                result[i++] = value;
            } catch (ClassCastException e) {
                throw new TechnicalException(
                        CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS,
                        MessageFormat.format(
                                "Error in casting [{0}] parameter to [{1}] type of config [{2}]: [{3}]",
                                parameter,
                                parameterTypes[i],
                                microprofileRestClientJobConfig.getKey(MicroprofileRestClientJobConfig.CONFIG_KEY_PARAMETERS),
                                e.getLocalizedMessage()),
                        e);
            }
        }
        return result;
    }

    private Object resolveMethod(String parameter, Class<?> parameterType) throws TechnicalException {
        String methodDef = StringUtils.substringBetween(parameter, METHOD_CALL_PREFIX, METHOD_CALL_SUFFIX);
        int lastDot = StringUtils.lastIndexOf(methodDef, ".");
        if (StringUtils.INDEX_NOT_FOUND == lastDot || methodDef.length() == lastDot) {
            throw new TechnicalException(
                    CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS,
                    MessageFormat.format(
                            "Invalid method definition in [{0}] parameter of config [{1}]",
                            parameter,
                            microprofileRestClientJobConfig.getKey(MicroprofileRestClientJobConfig.CONFIG_KEY_PARAMETERS)));
        }
        String methodName = methodDef.substring(lastDot + 1);
        String className = methodDef.substring(0, lastDot);

        try {
            Class<?> clazz = ClassUtils.getClass(className);
            Method method = clazz.getMethod(methodName);
            Object value = method.invoke(null);
            return parameterType.cast(value);
        } catch (Exception e) {
            throw new TechnicalException(
                    CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS,
                    MessageFormat.format(
                            "Error in resolving [{0}] static method value to [{1}] type of config [{2}]: [{3}]",
                            parameter,
                            parameterType,
                            microprofileRestClientJobConfig.getKey(MicroprofileRestClientJobConfig.CONFIG_KEY_PARAMETERS),
                            e.getLocalizedMessage()),
                    e);
        }
    }

    private boolean isMethodCall(String value) {
        return StringUtils.startsWith(value, METHOD_CALL_PREFIX) && StringUtils.endsWith(value, METHOD_CALL_SUFFIX);
    }
}
