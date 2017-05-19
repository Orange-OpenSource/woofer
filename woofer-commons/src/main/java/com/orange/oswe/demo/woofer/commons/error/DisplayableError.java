/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.commons.error;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an error description
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DisplayableError {

	@JsonIgnore
	private final ErrorCode status;

	@JsonIgnore
	private final Throwable error;
	
	@JsonIgnore
	private String description;

	public DisplayableError(ErrorCode status, Throwable error) {
		this.status = status;
		this.error = error;
	}
	
	public DisplayableError(ErrorCode status, String description) {
		this.status = status;
		this.error = null;
		this.description = description;
	}

	/**
	 * The error message
	 */
	@JsonProperty(required = false)
	public String getDescription() {
		if(description != null) {
			// overriden
			return description;
		}
		return error == null ? null : error.getMessage();
	}
	
	/**
	 * The error type code
	 */
	@JsonProperty(value="code", required = true)
	public int getStatusCode() {
		return status.getCode();
	}

	/**
	 * The error type name
	 */
	@JsonProperty(value="message", required = true)
	public String getStatusName() {
		return status.name();
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@JsonIgnore
	public ErrorCode getStatus() {
		return status;
	}
	
	@Override
	public String toString() {
		return "Error ["+status+"]: "+getDescription();
	}
}
