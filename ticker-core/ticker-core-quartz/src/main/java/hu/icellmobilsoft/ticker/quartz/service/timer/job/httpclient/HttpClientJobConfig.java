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

import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.ticker.quartz.service.timer.job.AbstractJobConfig;
import io.smallrye.config.SmallRyeConfig;

/**
 * Configuration keys for {@code ticker.timer.job._key_.actionClass:
 * hu.icellmobilsoft.ticker.quartz.service.timer.job.httpclient.HttpClientJob} value <br>
 *
 * keys:<br>
 * {@code ticker.timer.job._key_.config.baseUrl}: url which is called by the job <br>
 * <br>
 * {@code ticker.timer.job._key_.config.method}: http method <b>without</b> case-sensitive <br>
 * <br>
 * {@code ticker.timer.job._key_.config.headers}: it is optional! http headers map in the call. If contentType is not defined it will be
 * application/xml, If accept is not defined it will be application/xml! <br>
 * <br>
 * {@code ticker.timer.job._key_.config.body}: it is optional! If http method allow body, it is defined here like json or xml (important that it must
 * be in pari with contentType). Or it can be a reference to a static method on classpath with format
 * {@code {hu.icellmobilsoft.ticker.common.util.version.VersionUtil.knownVersions}} <br>
 * <br>
 * {@code ticker.timer.job._key_.config.queryParams}: query parametes of the http call in key-value pair <br>
 * <br>
 *
 * example:
 *
 * <pre>
 *     <code>
 * ticker:
 *   timer:
 *     activeJobs:
 *       - _key_
 *     job:
 *       _key_:
 *         code: TEST_APACHE_HTTP_CLIENT
 *         cron: "0 0/6 * * * ? *"
 *         actionClass: hu.icellmobilsoft.ticker.quartz.service.timer.job.httpclient.HttpClientJob
 *         config:
 *              baseUrl: http://localhost:8080/test/ticker
 *              method: PoST
 *              body: "&amp;{hu.icellmobilsoft.ticker.common.util.version.BaseRequestUtil.generate}"
 *              headers:
 *                  Content-Type: "application/xml"
 *                  Accept: "application/json"
 *              queryParams:
 *                  testString: value
 *                  testInteger: 1000
 *                  testLong: 50000
 *                  testBoolean: true
 *                  testOffsetDateTime: 2023-06-07T13:45:27.893013372Z
 * </code>
 * </pre>
 *
 * @author peter2.szabo
 * @since 1.1.0
 */
@Dependent
public class HttpClientJobConfig extends AbstractJobConfig {

    /**
     * Config key: {@value }
     */
    public static final String CONFIG_KEY_BASE_URL = "baseUrl";

    /**
     * Config key: {@value }
     */
    public static final String CONFIG_KEY_METHOD = "method";

    /**
     * Config key: {@value }
     */
    public static final String CONFIG_KEY_BODY = "body";

    /**
     * Config key: {@value }
     */
    public static final String CONFIG_KEY_QUERYPARAMETER_MAP = "queryParams";

    /**
     * Config key: {@value }
     */
    public static final String CONFIG_KEY_HEADERS_MAP = "headers";

    @Inject
    SmallRyeConfig config;

    /**
     * @return Config value of {@value #CONFIG_PATTERN}, where {0} is {@link #configKey}, {1} is {@value #CONFIG_KEY_BASE_URL}
     */
    public String baseUrl() {
        return config.getValue(getKey(CONFIG_KEY_BASE_URL), String.class);
    }

    /**
     * @return Config value of {@value #CONFIG_PATTERN}, where {0} is {@link #configKey}, {1} is {@value #CONFIG_KEY_METHOD}
     */
    public String method() {
        return config.getValue(getKey(CONFIG_KEY_METHOD), String.class);
    }

    /**
     * @return Config value of {@value #CONFIG_PATTERN}, where {0} is {@link #configKey}, {1} is {@value #CONFIG_KEY_BASE_URL}
     */
    public String body() {
        return config.getOptionalValue(getKey(CONFIG_KEY_BODY), String.class).orElse(StringUtils.EMPTY);
    }

    /**
     * @return Config value of {@value #CONFIG_PATTERN}, where {0} is {@link #configKey}, {1} is {@value #CONFIG_KEY_QUERYPARAMETER_MAP}
     */
    public Map<String, String> parameters() {
        return config.getOptionalValues(getKey(CONFIG_KEY_QUERYPARAMETER_MAP), String.class, String.class).orElse(new HashMap<>());
    }

    /**
     * @return Config value of {@value #CONFIG_PATTERN}, where {0} is {@link #configKey}, {1} is {@value #CONFIG_KEY_HEADERS_MAP}
     */
    public Map<String, String> headers() {
        return config.getOptionalValues(getKey(CONFIG_KEY_HEADERS_MAP), String.class, String.class).orElse(new HashMap<>());
    }

}
