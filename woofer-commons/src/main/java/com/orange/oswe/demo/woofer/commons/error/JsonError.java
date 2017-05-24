/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.commons.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON representation of an error
 * <p>
 * Based on <a href="https://developer.orange.com/apis/authentication-fr/api-reference#errors">common Orange Partner error codes</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class JsonError {

    /**
     * The error code is represented by two {@link JsonError} attributes: {@code message} (the enum name) and {@code code} (the enum integer code)
     */
    private final ErrorCode errorCode;

    private String description;

    JsonError(ErrorCode errorCode, Throwable error) {
        this(errorCode, error == null ? null : error.getMessage());
    }

    @JsonCreator
    JsonError(@JsonProperty(value = "message", required = true) ErrorCode errorCode, @JsonProperty(value = "description", required = true) String description) {
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

    /**
     * The error type @{@link ErrorCode#getCode() code}
     * <p>
     * It is also serialized in the JSON error representation
     */
    @JsonProperty(value = "code", required = true)
    public int getCode() {
        return errorCode.getCode();
    }

    /**
     * Setter to override the description
     */
    void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty(value = "message", required = true)
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "Error [" + errorCode + "]: " + getDescription();
    }
}
