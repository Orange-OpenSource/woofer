package com.orange.oswe.demo.woofer.commons.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Feign {@link ErrorDecoder decoder} of Http error responses with an array of {@link RestError} as body
 */
public class RestErrorDecoder implements ErrorDecoder {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = String.format("status %d reading %s", response.status(), methodKey);
        List<RestError> errors = null;
        if(response.body() != null) {
            try {
                RestError[] arrayOfErrors = mapper.readValue(response.body().asReader(), RestError[].class);
                if (arrayOfErrors != null && arrayOfErrors.length > 0) {
                    errors = Arrays.asList(arrayOfErrors);
                }
            } catch (IOException ioe) {
                // no op
            }
        }
        return new RestException(HttpStatus.valueOf(response.status()), message, errors);
    }
}