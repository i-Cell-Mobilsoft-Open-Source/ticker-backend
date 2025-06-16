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
package hu.icellmobilsoft.ticker.quartz.service.exception;

/**
 * Configuration validation error prevents the application from starting
 *
 * @author tamas.cserhati
 * @since 1.6.0
 *
 */
public class ConfigurationValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_EXCEPTION_MESSAGE = "Job configuration error: see logs for details!";

    /**
     * constructor with root exception and default message
     * 
     * @param exception
     *            the root cause to set
     */
    public ConfigurationValidationException(Throwable exception) {
        super(DEFAULT_EXCEPTION_MESSAGE, exception);
    }

    /**
     * constructor with custom message and root cause
     * 
     * @param message
     *            the message to set
     * @param exception
     *            the root cause to set
     */
    public ConfigurationValidationException(String message, Throwable exception) {
        super(message, exception);
    }
}
