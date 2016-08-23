package com.helix.sample.error;

import io.helixservice.feature.restservice.controller.Request;
import io.helixservice.feature.restservice.controller.Response;
import io.helixservice.feature.restservice.error.jsonapi.ErrorData;
import io.helixservice.feature.restservice.error.jsonapi.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorHandlers {
    private static final Logger LOG = LoggerFactory.getLogger(ErrorHandlers.class);

    private static final int HTTP_NOT_FOUND = 404;
    private static final int HTTP_INTERNAL_SERVER_ERROR = 500;

    public static Response<ErrorResponse> requestFailedException(Request request, RequestFailedException e) {
        ErrorResponse errorResponse = new ErrorResponse(HTTP_NOT_FOUND,
                new ErrorData("-20480", "RequestFailed", e.getMessage()));

        LOG.error(errorResponse.toString(), e);

        return Response.jsonAPIErrorResponse(errorResponse);
    }

    public static Response<byte[]> anyOtherException(Request request, Throwable throwable) {
        String errorMessage = "Unexpected exception " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
        LOG.error(errorMessage, throwable);

        return Response.fromHttpStatusCode(HTTP_INTERNAL_SERVER_ERROR, errorMessage.getBytes());
    }
}
