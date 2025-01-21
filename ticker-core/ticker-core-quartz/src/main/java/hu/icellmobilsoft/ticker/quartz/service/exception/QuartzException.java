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

import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * Common exception for Scheduler module.
 *
 * @author imre.scheffer
 * @author mate.biro
 * @since 0.1.0
 *
 */
public class QuartzException extends BaseException {

    private static final long serialVersionUID = 1L;

    private static final CoffeeFaultType FAULT_TYPE = CoffeeFaultType.OPERATION_FAILED;

    /**
     * Constructor with message
     *
     * @param message
     *            exception message
     */
    public QuartzException(String message) {
        super(FAULT_TYPE, message);
    }

    /**
     * Constructor with message and throwable
     *
     * @param message
     *            exception message
     * @param e
     *            throwable
     */
    public QuartzException(String message, Throwable e) {
        super(FAULT_TYPE, message, e);
    }
}
