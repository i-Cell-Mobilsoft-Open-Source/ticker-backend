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

import java.util.List;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.common.commonservice.BONotFound;
import hu.icellmobilsoft.coffee.dto.common.commonservice.BusinessFault;
import hu.icellmobilsoft.coffee.dto.common.commonservice.InvalidRequestFault;
import hu.icellmobilsoft.coffee.dto.common.commonservice.TechnicalFault;
import hu.icellmobilsoft.coffee.dto.common.commonservice.ValidationType;
import hu.icellmobilsoft.coffee.dto.exception.AccessDeniedException;
import hu.icellmobilsoft.coffee.dto.exception.BONotFoundException;
import hu.icellmobilsoft.coffee.dto.exception.ServiceUnavailableException;
import hu.icellmobilsoft.coffee.dto.exception.XMLValidationError;
import hu.icellmobilsoft.coffee.rest.exception.IExceptionMessageTranslator;
import hu.icellmobilsoft.coffee.rest.validation.xml.exception.XsdProcessingException;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * Exception mapper for handled exception throwing
 *
 * @author speter555
 * @since 1.1.0
 *
 */
@Provider
@Dependent
public class RestExceptionMapper implements ExceptionMapper<BaseException> {

    @Inject
    @ThisLogger
    private AppLogger log;

    @Inject
    private IExceptionMessageTranslator exceptionMessageTranslator;

    @Override
    public Response toResponse(BaseException e) {
        log.error("Known error: ", e);
        log.writeLogToError();

        if (e instanceof BONotFoundException) {
            BONotFound dto = new BONotFound();
            exceptionMessageTranslator.addCommonInfo(dto, e);
            return Response.status(IExceptionMessageTranslator.HTTP_STATUS_I_AM_A_TEAPOT).entity(dto).build();
        } else if (e instanceof XsdProcessingException xsdProcessingException) {
            InvalidRequestFault dto = new InvalidRequestFault();
            exceptionMessageTranslator.addCommonInfo(dto, e);
            addValidationErrors(dto, xsdProcessingException.getErrors());
            return Response.status(Response.Status.BAD_REQUEST).entity(dto).build();
        } else if (e instanceof AccessDeniedException) {
            BusinessFault dto = new BusinessFault();
            exceptionMessageTranslator.addCommonInfo(dto, e, e.getFaultTypeEnum());
            return Response.status(Response.Status.UNAUTHORIZED).entity(dto).build();
        } else if (e instanceof ServiceUnavailableException) {
            BusinessFault dto = new BusinessFault();
            exceptionMessageTranslator.addCommonInfo(dto, e, e.getFaultTypeEnum());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(dto).build();
        } else {
            // BaseException
            TechnicalFault dto = new TechnicalFault();
            exceptionMessageTranslator.addCommonInfo(dto, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(dto).build();
        }
    }

    private void addValidationErrors(InvalidRequestFault dto, List<XMLValidationError> errors) {
        if (errors != null && errors.size() > 0) {
            for (XMLValidationError error : errors) {
                ValidationType valType = new ValidationType();
                valType.setError(error.getError());
                valType.setColumnNumber(error.getColumnNumber());
                valType.setLineNumber(error.getLineNumber());
                dto.getError().add(valType);
            }
        }
    }
}
