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
package hu.icellmobilsoft.ticker.common.rest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import jakarta.enterprise.inject.Model;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.rest.rest.BaseRestService;
import hu.icellmobilsoft.ticker.common.api.system.ISystemRest;

/**
 * System rest endpoint implementations
 *
 * @author adam.magyari
 * @since 0.3.0
 */
@Model
public class SystemRest extends BaseRestService implements ISystemRest {

    @Inject
    @ThisLogger
    AppLogger log;

    @Override
    public String versionInfo() throws BaseException {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
            StringBuilder version = new StringBuilder();
            if (inputStream != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line;
                while ((line = br.readLine()) != null) {
                    version.append(line);
                    version.append("\n");
                }
            }
            return version.toString();
        } catch (Exception e) {
            throw baseExceptionWithLogging(e);
        }
    }
}
