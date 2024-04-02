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
package hu.icellmobilsoft.ticker.common.rest.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;

/**
 * Converter for OffsetDateTime parameter
 *
 * @author mate.biro
 * @since 0.1.0
 */
@Provider
public class OffsetDateTimeParamConverterProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType != null && rawType.equals(OffsetDateTime.class)) {
            return new ParamConverter<T>() {

                @Override
                public T fromString(String value) {
                    if (StringUtils.isNotBlank(value)) {
                        return (T) OffsetDateTime.parse(value);
                    } else {
                        return null;
                    }
                }

                @Override
                public String toString(T value) {
                    if (value != null && value instanceof OffsetDateTime) {
                        return ((OffsetDateTime) value).toString();
                    } else {
                        return null;
                    }
                }
            };
        }
        return null;
    }
}
