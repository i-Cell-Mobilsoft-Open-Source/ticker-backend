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
package hu.icellmobilsoft.ticker.core.quartz.service.version;

import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseRequest;
import hu.icellmobilsoft.coffee.dto.common.commonservice.ContextType;
import hu.icellmobilsoft.coffee.se.util.string.RandomUtil;
import hu.icellmobilsoft.coffee.tool.utils.date.DateUtil;

/**
 * Test util for generate {@link BaseRequest}
 *
 * @since 0.4.0
 * @author peter2.szabo
 */
public class BaseRequestUtil {

    /**
     * Generate a {@link BaseRequest} instance
     *
     * @return base request instance
     */
    public static BaseRequest generate() {
        return new BaseRequest()
                .withContext(new ContextType().withRequestId(RandomUtil.generateId()).withTimestamp(DateUtil.nowUTCTruncatedToMillis()));
    }

}
