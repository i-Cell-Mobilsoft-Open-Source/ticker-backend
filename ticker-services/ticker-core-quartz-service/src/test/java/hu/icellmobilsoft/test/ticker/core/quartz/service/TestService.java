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
package hu.icellmobilsoft.test.ticker.core.quartz.service;

import java.util.List;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TestService extends AbstractQuarkusTest {

    @Override
    protected List<String> getExpectedJobKeys() {
        return List.of("TEST_REST", "TEST_REST_2", "TEST_APACHE_HTTP_CLIENT_GET", "TEST_APACHE_HTTP_CLIENT_POST", "TEST_CUSTOM_JOB");
    }
}
