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
package hu.icellmobilsoft.ticker.metrics.constants;

/**
 * All metrics constants
 * 
 * @author imre.scheffer
 * @author mate.biro
 * @since 0.2.0
 */
public interface MetricsConstants {

    /**
     * Tag constants which is handled by microprofile-metrics
     *
     * @author imre.scheffer
     *
     */
    interface Tag {
        /**
         * config tag
         */
        String CONFIG_KEY = "configKey";
    }

    /**
     * Quartz metrics constants
     */
    interface Quartz {

        /**
         * Quartz metrics names
         */
        interface Name {
            /**
             * Quartz job next fire time
             */
            String QUARTZ_JOB_NEXT_FIRE_TIME = "quartz_job_next_fire_time";

            /**
             * Quartz job prev fire time
             */
            String QUARTZ_JOB_PREV_FIRE_TIME = "quartz_job_prev_fire_time";

            /**
             * Quartz job run time
             */
            String QUARTZ_JOB_RUN_TIME = "quartz_job_run_time";
        }

        /**
         * Quartz metrics descriptions
         */
        interface Description {
            /**
             * Quartz job next fire time
             */
            String QUARTZ_JOB_NEXT_FIRE_TIME_DESCRIPTION = "Quartz job next fire time";

            /**
             * Quartz job next fire time
             */
            String QUARTZ_JOB_PREV_FIRE_TIME_DESCRIPTION = "Quartz job prev fire time";

            /**
             * Quartz job run time
             */
            String QUARTZ_JOB_RUN_TIME_DESCRIPTION = "Quartz job run time";
        }
    }
}
