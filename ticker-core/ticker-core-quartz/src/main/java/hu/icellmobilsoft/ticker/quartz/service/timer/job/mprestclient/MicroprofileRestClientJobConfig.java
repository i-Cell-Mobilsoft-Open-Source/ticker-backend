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

import java.util.Collections;
import java.util.List;

import jakarta.enterprise.context.Dependent;

import org.eclipse.microprofile.config.ConfigProvider;

import hu.icellmobilsoft.ticker.quartz.service.timer.job.AbstractJobConfig;

/**
 * Configuration keys for {@code ticker.timer.job._key_.actionClass:
 * hu.icellmobilsoft.ticker.quartz.service.timer.job.mprestclient.MicroprofileRestClientJob} value <br>
 *
 * keys:<br>
 * {@code ticker.timer.job._key_.config.mpRestClientClass}: MP restClient interface fully qualified name <br>
 * <br>
 * {@code ticker.timer.job._key_.config.method}: method to invoke on restclient class, format: method(param1,param2...), params must be fully
 * qualified types i.e. {@code testMethod(java.lang.String, hu.icellmobilsoft.ticker.SomeEnum)} <br>
 * <br>
 * {@code ticker.timer.job._key_.config.parameters}: list of parameters for rest, can be literal or reference to a static method on classpath with
 * format {@code {hu.icellmobilsoft.ticker.common.util.version.VersionUtil.knownVersions}} <br>
 * <br>
 *
 * example:
 *
 * <pre>
 * ticker:
 *   timer:
 *     activeJobs:
 *       - _key_
 *     job:
 *       _key_:
 *         code: DUMMY_JOB_CODE
 *         cron: "0 0/6 * * * ? *"
 *         actionClass: hu.icellmobilsoft.ticker.quartz.service.timer.job.mprestclient.MicroprofileRestClientJob
 *         config:
 *           mpRestClientClass: hu.icellmobilsoft.ticker.quartz.service.rest.test.api.IDummyRest #RestClient to call
 *           method: getDummy(java.lang.String,java.util.List)
 *           parameters:
 *             - "null"
 *             - "{hu.icellmobilsoft.ticker.common.util.version.VersionUtil.knownVersions}" # static method call
 * </pre>
 *
 * @author Imre Scheffer
 * @author mate.biro
 * @since 0.1.0
 */
@Dependent
public class MicroprofileRestClientJobConfig extends AbstractJobConfig {

    /**
     * Config key: {@value }
     */
    public static final String CONFIG_KEY_MP_REST_CLIENT_CLASS = "mpRestClientClass";

    /**
     * Config key: {@value }
     */
    public static final String CONFIG_KEY_METHOD = "method";

    /**
     * Config key: {@value }
     */
    public static final String CONFIG_KEY_PARAMETERS = "parameters";

    /**
     * @return Config value of {@value #CONFIG_PATTERN}, where {0} is {@link #configKey}, {1} is {@value #CONFIG_KEY_MP_REST_CLIENT_CLASS}
     */
    public String mpRestClientClass() {
        return ConfigProvider.getConfig().getValue(getKey(CONFIG_KEY_MP_REST_CLIENT_CLASS), String.class);
    }

    /**
     * @return Config value of {@value #CONFIG_PATTERN}, where {0} is {@link #configKey}, {1} is {@value #CONFIG_KEY_METHOD}
     */
    public String method() {
        return ConfigProvider.getConfig().getValue(getKey(CONFIG_KEY_METHOD), String.class);
    }

    /**
     * @return Config value of {@value #CONFIG_PATTERN}, where {0} is {@link #configKey}, {1} is {@value #CONFIG_KEY_PARAMETERS}
     */
    public List<String> parameters() {
        return ConfigProvider.getConfig().getOptionalValues(getKey(CONFIG_KEY_PARAMETERS), String.class).orElse(Collections.emptyList());
    }

}
