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
package hu.icellmobilsoft.ticker.quartz.service.timer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

import hu.icellmobilsoft.ticker.quartz.service.timer.config.ITimerConfig;

/**
 * Qualifier for instantiating {@link ITimerConfig}
 *
 * @author Imre Scheffer
 * @author mate.biro
 * @since 0.1.0
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE })
public @interface Timer {

    /**
     * Config key of the desired Timer settings. <br>
     * ie. if connection details are defined in the microprofile-*.yml by the keys: {@code ticker.timer.test.*=...} then configKey should be "test"
     *
     * @return Config key
     */
    @Nonbinding
    String configKey();

    /**
     * Supports inline instantiation of the {@link Timer} qualifier.
     *
     * @author Imre Scheffer
     * @author mate.biro
     *
     */
    final class Literal extends AnnotationLiteral<Timer> implements Timer {

        private static final long serialVersionUID = 1L;

        /**
         * Config key
         */
        private final String configKey;

        /**
         * Constructor
         * 
         * @param configKey
         *            config key
         */
        public Literal(String configKey) {
            this.configKey = configKey;
        }

        /**
         * Get config key
         * 
         * @return config key
         */
        @Nonbinding
        public String configKey() {
            return configKey;
        }
    }
}
