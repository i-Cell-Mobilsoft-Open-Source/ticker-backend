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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import hu.icellmobilsoft.coffee.dto.common.LogConstants;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.BusinessException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.rest.apache.BaseApacheHttpClient;
import hu.icellmobilsoft.coffee.se.logging.mdc.MDC;

/**
 * Apache http client
 *
 * @author speter555
 * @since 1.1.0
 */
@Dependent
@Named
public class ApacheHttpClient extends BaseApacheHttpClient {

    private final HashMap<String, String> headers = new HashMap<>();

    @Override
    protected void beforeAll(HttpRequestBase request) {
        if (hu.icellmobilsoft.coffee.se.logging.mdc.MDC.get(LogConstants.LOG_SESSION_ID) != null) {
            request.setHeader(LogConstants.LOG_SESSION_ID, MDC.get(LogConstants.LOG_SESSION_ID));
        }
        if (!headers.isEmpty()) {
            headers.forEach(request::setHeader);
        }
    }

    /**
     * Add header the parameters
     *
     * @param key
     *            header's key
     * @param value
     *            header's value
     * @throws BaseException
     *             if any error occurs
     */
    public void addToHeader(String key, String value) throws BaseException {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            throw new BusinessException(
                    MessageFormat.format("Error during add header param to http call. Key is: [{0}], Value is: [{1}]", key, value));
        }
        headers.put(key, value);
    }

    /**
     *
     * Send http GET request.
     *
     * @param url
     *            URL
     * @throws BaseException
     *             if any exception occurs
     */
    public void sendClientGet(String url) throws BaseException {
        HttpResponse response = super.sendClientBaseGet(url);
        handleResponse(response);
    }

    /**
     * Send http POST request.
     *
     * @param url
     *            URL
     * @param contentType
     *            content type
     * @param entityObject
     *            http request entity
     * @throws BaseException
     *             if any exception occurs
     */
    public void sendClientPost(String url, ContentType contentType, byte[] entityObject) throws BaseException {
        HttpResponse response = super.sendClientBasePost(url, contentType, entityObject);
        handleResponse(response);
    }

    /**
     * Send http PUT request.
     *
     * @param url
     *            URL
     * @param contentType
     *            content type
     * @param entityObject
     *            http request entity
     * @throws BaseException
     *             if any exception occurs
     */
    public void sendClientPut(String url, ContentType contentType, byte[] entityObject) throws BaseException {
        HttpResponse response = super.sendClientBasePut(url, contentType, entityObject);
        handleResponse(response);
    }

    /**
     * Send http DELETE request with response logging
     *
     * @param url
     *            URL
     * @throws BaseException
     *             if any exception occurs
     */
    public void sendClientDelete(String url) throws BaseException {
        HttpResponse response = super.sendClientBaseDelete(url);
        handleResponse(response);
    }

    private void handleResponse(HttpResponse response) throws BaseException {
        try {
            byte[] byteEntity = EntityUtils.toByteArray(response.getEntity());
            // loggoljuk a response-t
            logResponse(response, byteEntity);

        } catch (IOException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "IOException in call: " + e.getLocalizedMessage(), e);
        }
    }
}
