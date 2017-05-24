package com.orange.oswe.demo.woofer.commons.error;

import java.util.List;

import org.springframework.http.HttpStatus;

/**
 * Exception built by the {@link JsonErrorDecoder} upon http error using Feign
 * <p>
 * If available in the response body, contains the list of {@link JsonError}.
 */
public class HttpException extends RuntimeException {
    private final HttpStatus status;
    private final List<JsonError> errors;

    HttpException(HttpStatus status, String message, List<JsonError> errors) {
        super(buildMessage(message, errors));
        this.status = status;
        this.errors = errors;
    }

    private static String buildMessage(String message, List<JsonError> errors) {
        if(errors == null || errors.isEmpty()) {
            return message;
        }
        return message + " - " + errors;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public List<JsonError> getErrors() {
        return errors;
    }

    /**
     * Returns the main error code
     */
    public ErrorCode getMainCode() {
        if(errors == null || errors.isEmpty()) {
            return ErrorTranslator.getDefaultCode(getStatus());
        }
        return errors.get(0).getErrorCode();
    }
}
