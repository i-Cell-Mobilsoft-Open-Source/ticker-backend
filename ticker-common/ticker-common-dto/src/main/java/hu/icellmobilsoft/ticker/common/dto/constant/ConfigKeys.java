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
package hu.icellmobilsoft.ticker.common.dto.constant;

import hu.icellmobilsoft.coffee.module.configdoc.ConfigDoc;

/**
 * Application configuration keys
 * 
 * @author tamas.cserhati
 * @since 1.6.0
 */
@ConfigDoc
public interface ConfigKeys {

    /**
     * ticker config keys
     */
    interface Ticker {
        
        /**
         * the root key
         */
        @ConfigDoc(exclude = true)
        String SERVICE_NAME = "ticker";

        /**
         * prefix
         */
        @ConfigDoc(exclude = true)
        String PREFIX_TICKER = SERVICE_NAME + ".";

        /**
         * Configuration specific keys
         */
        interface Config {
            
            /**
             * config subkey
             */
            @ConfigDoc(exclude = true)
            String PREFIX_CONFIG = PREFIX_TICKER + "config.";

            /**
             * Enabling configuration validation on startup
             */
            @ConfigDoc(defaultValue = Defaults.TICKER_CONFIG_VALIDATION_DEFAULT)
            String VALIDATION = PREFIX_CONFIG + "validation";
        }

        /**
         * Default values
         */
        interface Defaults {
            /**
             * {@value #TICKER_CONFIG_VALIDATION_DEFAULT}
             */
            @ConfigDoc(exclude = true)
            String TICKER_CONFIG_VALIDATION_DEFAULT = "false";
        }
    }

}
