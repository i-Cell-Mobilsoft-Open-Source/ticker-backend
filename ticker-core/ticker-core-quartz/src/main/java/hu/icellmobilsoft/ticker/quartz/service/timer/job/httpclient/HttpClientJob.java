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

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Map;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.tool.gson.JsonUtil;
import hu.icellmobilsoft.coffee.tool.utils.marshalling.MarshallingUtil;
import hu.icellmobilsoft.ticker.quartz.service.quartz.BaseCronJob;
import hu.icellmobilsoft.ticker.quartz.service.timer.job.JobRegistrar;

/**
 * Http client Job
 *
 * @author peter2.szabo
 * @since 1.1.0
 */
@Dependent
@DisallowConcurrentExecution
public class HttpClientJob extends BaseCronJob {

    private static final String METHOD_CALL_PREFIX = "&{";
    private static final String METHOD_CALL_SUFFIX = "}";

    /**
     * Config for the job
     */
    @Inject
    HttpClientJobConfig httpClientJobConfig;

    /** {@inheritDoc} */
    @ActivateRequestContext
    @Override
    public void executeJob(JobExecutionContext context) throws BaseException {
        httpClientJobConfig.setConfigKey(context.getJobDetail().getJobDataMap().getString(JobRegistrar.CONFIG_KEY));
        Map<String, String> headers = getHeaders();
        callEndpoint(headers, getBody(headers.get(HttpHeaders.CONTENT_TYPE)));
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = httpClientJobConfig.headers();
        if (StringUtils.isBlank(headers.get(HttpHeaders.ACCEPT))) {
            headers.put(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML);
        }
        if (StringUtils.isBlank(headers.get(HttpHeaders.CONTENT_TYPE))) {
            headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML);
        }
        return headers;
    }

    private String getBody(String contentType) throws TechnicalException {
        String body = httpClientJobConfig.body();
        if (StringUtils.equalsIgnoreCase("null", body)) {
            body = null;
        } else if (isMethodCall(body)) {
            Object requestObject = resolveMethod(body);
            if (contentType.contains("json")) {
                body = JsonUtil.toJson(requestObject);
            } else {
                body = MarshallingUtil.marshall(requestObject);
            }
        }
        return body;
    }

    private void callEndpoint(Map<String, String> headers, String body) throws BaseException {
        StringBuilder baseUrlBuilder = new StringBuilder(httpClientJobConfig.baseUrl());
        String method = httpClientJobConfig.method();
        Map<String, String> queryParameters = httpClientJobConfig.parameters();

        Instance<ApacheHttpClient> apacheHttpClientInstance = CDI.current().select(ApacheHttpClient.class);
        ApacheHttpClient httpClient = apacheHttpClientInstance.get();

        try {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpClient.addToHeader(entry.getKey(), entry.getValue());
            }

            baseUrlBuilder.append("?");
            for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
                baseUrlBuilder.append(entry.getKey());
                baseUrlBuilder.append("=");
                baseUrlBuilder.append(entry.getValue());
                baseUrlBuilder.append("&");
            }
            if (baseUrlBuilder.toString().endsWith("&")) {
                baseUrlBuilder.deleteCharAt(baseUrlBuilder.length() - 1);
            }

            String baseUrl = baseUrlBuilder.toString();
            if (StringUtils.equalsIgnoreCase(method, "GET")) {
                httpClient.sendClientGet(baseUrl);
            } else if (StringUtils.equalsIgnoreCase(method, "POST")) {
                httpClient.sendClientPost(baseUrl, ContentType.parse(headers.get(HttpHeaders.CONTENT_TYPE)), body != null ? body.getBytes() : null);
            } else if (StringUtils.equalsIgnoreCase(method, "PUT")) {
                httpClient.sendClientPut(baseUrl, ContentType.parse(headers.get(HttpHeaders.CONTENT_TYPE)), body != null ? body.getBytes() : null);
            } else if (StringUtils.equalsIgnoreCase(method, "DELETE")) {
                httpClient.sendClientDelete(baseUrl);
            } else {
                throw new TechnicalException(MessageFormat.format("Wrong method. Use Get, Post, Put or Delete! Method is : [{0}]", method));
            }
        } catch (Exception e) {
            throw new TechnicalException(
                    CoffeeFaultType.GENERIC_EXCEPTION,
                    MessageFormat.format(
                            "Error in call baseUrl: [{0}], with method [{1}], and with body: [{2}] and with queryParameters: [{3}]",
                            baseUrlBuilder,
                            method,
                            body,
                            queryParameters),
                    e);
        } finally {
            if (httpClient != null) {
                apacheHttpClientInstance.destroy(httpClient);
            }
        }
    }

    private boolean isMethodCall(String value) {
        return StringUtils.startsWith(value, METHOD_CALL_PREFIX) && StringUtils.endsWith(value, METHOD_CALL_SUFFIX);
    }

    private Object resolveMethod(String parameter) throws TechnicalException {
        String methodDef = StringUtils.substringBetween(parameter, METHOD_CALL_PREFIX, METHOD_CALL_SUFFIX);
        int lastDot = StringUtils.lastIndexOf(methodDef, ".");
        if (StringUtils.INDEX_NOT_FOUND == lastDot || methodDef.length() == lastDot) {
            throw new TechnicalException(
                    CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS,
                    MessageFormat.format(
                            "Invalid method definition in [{0}] parameter of config [{1}]",
                            parameter,
                            httpClientJobConfig.getKey(HttpClientJobConfig.CONFIG_KEY_BODY)));
        }
        String methodName = methodDef.substring(lastDot + 1);
        String className = methodDef.substring(0, lastDot);

        try {
            Class<?> clazz = ClassUtils.getClass(className);
            Method method = clazz.getMethod(methodName);
            return method.invoke(null);
        } catch (Exception e) {
            throw new TechnicalException(
                    CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS,
                    MessageFormat.format(
                            "Error in resolving [{0}] static method, type of config [{1}]: [{2}]",
                            parameter,
                            httpClientJobConfig.getKey(HttpClientJobConfig.CONFIG_KEY_BODY),
                            e.getLocalizedMessage()),
                    e);
        }
    }

}
