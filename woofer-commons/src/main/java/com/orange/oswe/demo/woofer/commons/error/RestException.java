package com.orange.oswe.demo.woofer.commons.error;

import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Exception built by the {@link RestErrorDecoder} upon http error using Feign
 * <p>
 * If available in the response body, contains the list of {@link RestError}.
 */
public class RestException extends RuntimeException {
    private final HttpStatus status;
    private final List<RestError> errors;

    RestException(HttpStatus status, String message, List<RestError> errors) {
        super(buildMessage(message, errors));
        this.status = status;
        this.errors = errors;
    }

    private static String buildMessage(String message, List<RestError> errors) {
        if(errors == null || errors.isEmpty()) {
            return message;
        }
        return message + " - " + errors;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public List<RestError> getErrors() {
        return errors;
    }

    /**
     * Returns the main error code
     */
    public ErrorCode getMainCode() {
        return errors.isEmpty() ? null : errors.get(0).getErrorCode();
    }
}
