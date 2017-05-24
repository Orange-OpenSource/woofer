package com.orange.oswe.demo.woofer.commons.error;

import static java.lang.String.format;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

/**
 * Feign {@link ErrorDecoder decoder} of Http error responses with an array of {@link JsonError} as body
 */
public class JsonErrorDecoder implements ErrorDecoder {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = format("status %s reading %s", response.status(), methodKey);
        List<JsonError> errors = null;
        if(response.body() != null) {
            try {
                JsonError[] arrayOfErrors = mapper.readValue(response.body().asReader(), JsonError[].class);
                if (arrayOfErrors != null && arrayOfErrors.length > 0) {
                    errors = Arrays.asList(arrayOfErrors);
                }
            } catch (IOException e) {
            }
        }
        return new HttpException(HttpStatus.valueOf(response.status()), message, errors);
    }
}