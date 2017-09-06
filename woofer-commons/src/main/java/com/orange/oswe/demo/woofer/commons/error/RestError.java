/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.commons.error;

import com.fasterxml.jackson.annotation.*;

/**
 * JSON representation of an error
 * <p>
 * Based on <a href="https://developer.orange.com/apis/authentication-fr/api-reference#errors">common Orange Partner error codes</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestError {

    private final ErrorCode errorCode;

    private final String description;

    @JsonCreator
    RestError(@JsonProperty(value = "message", required = true) ErrorCode errorCode, @JsonProperty(value = "description", required = true) String description) {
        this.errorCode = errorCode;
        this.description = description;
    }

    /**
     * The error textual description
     */
    @JsonProperty
    public String getDescription() {
        return description;
    }

    @JsonIgnore
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @JsonProperty("message")
    public String getErrorName() {
        return errorCode.name();
    }

    @JsonProperty("code")
    public int getCode() {
        return errorCode.getCode();
    }

    @Override
    public String toString() {
        return "Error [" + errorCode + "]: " + getDescription();
    }
}
