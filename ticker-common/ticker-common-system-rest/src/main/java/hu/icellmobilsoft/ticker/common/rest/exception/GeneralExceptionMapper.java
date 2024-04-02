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
package hu.icellmobilsoft.ticker.common.rest.exception;

import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.ext.Provider;

import hu.icellmobilsoft.coffee.rest.exception.DefaultGeneralExceptionMapper;

/**
 * Exception mapper for non-handled exception throwing
 *
 * @author speter555
 * @since 1.1.0
 *
 */
@Provider
@Dependent
public class GeneralExceptionMapper extends DefaultGeneralExceptionMapper {

}
