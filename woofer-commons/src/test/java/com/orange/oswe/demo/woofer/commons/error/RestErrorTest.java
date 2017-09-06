package com.orange.oswe.demo.woofer.commons.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RestErrorTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(RestErrorTest.class);

    @Test
    public void error_should_serialize() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        RestError error = new RestError(ErrorCode.ResourceAccessDenied, "you shall not pass");
        LOGGER.info("serialized error: {}", mapper.writeValueAsString(error));
    }

    @Test
    public void error_should_deserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        RestError error = new RestError(ErrorCode.ResourceAccessDenied, "you shall not pass");
        String json = mapper.writeValueAsString(error);
        RestError error2 = mapper.readValue(json, RestError.class);
        LOGGER.info("deserialized error: {}", error2);
        Assert.assertEquals(error.getDescription(), error2.getDescription());
        Assert.assertEquals(error.getErrorCode(), error2.getErrorCode());
    }

}
