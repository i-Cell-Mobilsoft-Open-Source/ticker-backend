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
package hu.icellmobilsoft.ticker.quartz.service.restclient.listener;

import java.text.MessageFormat;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.configuration.ApplicationConfiguration;

import org.jboss.resteasy.client.jaxrs.internal.ClientRequestContextImpl;

/**
 * Rest endpoint connectTimeOut and readTimeout logging.
 *
 * @author istvan.peli
 * @since 0.4.0
 */
@Dependent
@Named
public class RestConfigLoggerFilter implements ClientRequestFilter {
    private static final String DEFAULT_VALUE = "default value";
    private static final String QUARKUS_KEY = "quarkus.rest-client.";
    private static final String REST_CONNECT_TIMEOUT_FORMAT = ".connect-timeout";
    private static final String REST_READ_TIMEOUT_FORMAT = ".read-timeout";

    @Inject
    @ThisLogger
    AppLogger log;

    @Inject
    ApplicationConfiguration config;

    @Override
    public void filter(ClientRequestContext clientRequestContext) {
        String configKey = "\"" + ((ClientRequestContextImpl) clientRequestContext).getInvocation().getClientInvoker().getDeclaring().getName()
                + "\"";

        String msg = "\n>> " + this.getClass().getName() + " request ->\n"
        // URL
                + "> url: [" + clientRequestContext.getMethod() + " " + clientRequestContext.getUri() + "]\n"
                // config
                + getConfigString(configKey);
        log.info(msg);
    }

    private String getConfigString(String configKey) {
        String connectTimeout = config.getOptionalValue(QUARKUS_KEY + configKey + REST_CONNECT_TIMEOUT_FORMAT, String.class).orElse(DEFAULT_VALUE);
        String readTimeout = config.getOptionalValue(QUARKUS_KEY + configKey + REST_READ_TIMEOUT_FORMAT, String.class).orElse(DEFAULT_VALUE);
        return MessageFormat.format("> config: [\n>    connectTimeout:[{0}]\n>    readTimeout:[{1}]]", connectTimeout, readTimeout);
    }
}
