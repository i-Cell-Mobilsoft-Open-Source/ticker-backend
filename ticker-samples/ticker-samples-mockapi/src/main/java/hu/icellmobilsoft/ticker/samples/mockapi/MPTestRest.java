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
package hu.icellmobilsoft.ticker.samples.mockapi;

import java.time.OffsetDateTime;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseRequest;
import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseResponse;
import hu.icellmobilsoft.ticker.common.dto.path.TickerPath;

/**
 * REST endpoint for system service functions.
 *
 * @author adam.magyari
 * @since 0.3.0
 */
@Path("/test")
@RegisterRestClient(configKey = "mockapi")
public interface MPTestRest {

    /**
     * Mock service GET endpoint
     * 
     * @param testString
     *            test string
     * @param testInteger
     *            test integer
     * @param testLong
     *            test long
     * @param testBoolean
     *            test boolean
     * @param testOffsetDateTime
     *            test offset datetime
     * @return response
     */
    @Operation(hidden = true)
    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML })
    BaseResponse getTest(@QueryParam(TickerPath.PARAM_TEST_STRING) String testString, @QueryParam(TickerPath.PARAM_TEST_INTEGER) Integer testInteger,
                         @QueryParam(TickerPath.PARAM_TEST_LONG) Long testLong, @QueryParam(TickerPath.PARAM_TEST_BOOLEAN) Boolean testBoolean,
                         @QueryParam(TickerPath.PARAM_TEST_OFFSET_DATE_TIME) OffsetDateTime testOffsetDateTime);

    /**
     * Mock service POST endpoint
     *
     * @param baseRequest
     *            base request
     * @param testString
     *            test string
     * @param testInteger
     *            test integer
     * @param testLong
     *            test long
     * @param testBoolean
     *            test boolean
     * @param testOffsetDateTime
     *            test offset datetime
     * @return response
     */
    @Operation(hidden = true)
    @POST
    @Consumes(value = { MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML })
    @Produces(value = { MediaType.APPLICATION_JSON, MediaType.TEXT_XML, MediaType.APPLICATION_XML })
    BaseResponse postTest(BaseRequest baseRequest, @QueryParam(TickerPath.PARAM_TEST_STRING) String testString,
            @QueryParam(TickerPath.PARAM_TEST_INTEGER) Integer testInteger, @QueryParam(TickerPath.PARAM_TEST_LONG) Long testLong,
            @QueryParam(TickerPath.PARAM_TEST_BOOLEAN) Boolean testBoolean,
            @QueryParam(TickerPath.PARAM_TEST_OFFSET_DATE_TIME) OffsetDateTime testOffsetDateTime);
}
