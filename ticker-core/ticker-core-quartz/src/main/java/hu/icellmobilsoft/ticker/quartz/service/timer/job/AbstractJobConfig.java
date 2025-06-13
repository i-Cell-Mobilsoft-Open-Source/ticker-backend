/*-
 * #%L
 * Ticker
 * %%
 * Copyright (C) 2024 - 2025 i-Cell Mobilsoft Zrt.
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

/**
 * abstract job config
 * 
 * @author tamas.cserhati
 * @since 1.6.0
 */
public abstract class AbstractJobConfig {

    /**
     * Config pattern
     */
    protected static final String CONFIG_PATTERN = "ticker.timer.job.{0}.config.{1}";

    private String configKey;

    /**
     * Get config key
     *
     * @return configKey
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * Set config key
     *
     * @param configKey
     *            config key
     */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    /**
     * @param part
     *            pattern 2nd config value
     * @return key string of {@value #CONFIG_PATTERN}, where {0} is {@link #configKey}, {1} is required configKey part
     */
    public String getKey(String part) {
        return MessageFormat.format(CONFIG_PATTERN, getConfigKey(), part);
    }

}
