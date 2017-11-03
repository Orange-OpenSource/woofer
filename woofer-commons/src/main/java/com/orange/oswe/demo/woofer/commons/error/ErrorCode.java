/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.commons.error;

import org.springframework.http.HttpStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Internal error codes
 * <p>
 * Compatible with Orange Partner codes (see <a href="https://developer.orange.com/apis/authentication-fr/api-reference#errors">Common Errors</a>)
 */
public enum ErrorCode {
	// ====================================================================================
	// === Orange Partner common codes
	// ====================================================================================
	/**
	 * Returned when an internal error occurred.
	 */
	@Doc("Returned when an internal error occurred. We are most probably working on it; try your request later")
	InternalError(HttpStatus.INTERNAL_SERVER_ERROR, 1), // NOSONAR
	/**
	 * The posted body is not well-formed and thus can not be parsed.
	 */
	@Doc("The posted body is not well-formed and thus can not be parsed.")
	InvalidRequestBody(HttpStatus.BAD_REQUEST, 22), // NOSONAR
	/**
	 * One or more query-string parameters are missing.
	 */
	@Doc("One or more query-string parameters are missing.")
	MissingParameter(HttpStatus.BAD_REQUEST, 27), // NOSONAR
	/**
	 * The requested service needs credentials, but none were provided.
	 */
	@Doc("The requested service needs credentials, but none were provided.")
	MissingCredentials(HttpStatus.UNAUTHORIZED, 40), // NOSONAR
	/**
	 * The requested service needs credentials, but the ones provided were invalid.
	 */
	@Doc("The requested service needs credentials, but the ones provided were invalid.")
	InvalidCredentials(HttpStatus.UNAUTHORIZED, 41), // NOSONAR
	/**
	 * The requested service needs credentials, and the ones provided were out-of-date.
	 */
	@Doc("The requested service needs credentials, and the ones provided were out-of-date.")
	ExpiredCredentials(HttpStatus.UNAUTHORIZED, 42), // NOSONAR
	/**
	 * The application that makes the request is not authorized to access this endpoint.
	 */
	@Doc("The application that makes the request is not authorized to access this endpoint.")
	ServiceAccessDenied(HttpStatus.FORBIDDEN, 50), // NOSONAR
	/**
	 * Returned when a request handler does not support a specific request method.
	 */
	@Doc("Returned when a request handler does not support a specific request method.")
	MethodNotSupported(HttpStatus.METHOD_NOT_ALLOWED, 61), // NOSONAR
	/**
	 * Returned when the request handler cannot generate a response that is
	 * acceptable by the client.
	 */
	@Doc("Returned when the request handler cannot generate a response that is acceptable by the client.")
	MediaTypeNotAcceptable(HttpStatus.NOT_ACCEPTABLE, 62), // NOSONAR
	/**
	 * The format of the posted body is not supported by the endpoint.
	 */
	@Doc("The format of the posted body is not supported by the endpoint.")
	MediaTypeNotSupported(HttpStatus.UNSUPPORTED_MEDIA_TYPE, 68), // NOSONAR
	/**
	 * The requested service is currently unavailable (for any technical or maintenance reason). Generally, this is a temporary state.
	 */
	@Doc("The requested service is currently unavailable (for any technical or maintenance reason). Generally, this is a temporary state.")
	ServiceUnavailable(HttpStatus.SERVICE_UNAVAILABLE, 5), // NOSONAR

	// ====================================================================================
	// === codes for Spring
	// ====================================================================================
	/**
	 * Request successful.
	 */
	@Doc("Request successful.")
	Success(HttpStatus.OK, 0), // NOSONAR
	/**
	 * Unknown bad request error.
	 */
	@Deprecated
	@Doc("Unknown bad request error.")
	BadRequest(HttpStatus.BAD_REQUEST, 1040), // NOSONAR
	/**
	 * Returned when the part of a "multipart/form-data" request identified by its
	 * name cannot be found.
	 */
	@Doc("Returned when the part of a 'multipart/form-data' request identified by its name cannot be found.")
	MissingRequestPart(HttpStatus.BAD_REQUEST, 23), // NOSONAR
	/**
	 * Returned when a parameter is invalid in the request.
	 */
	@Doc("Returned when a parameter (path or query) is invalid in the request.")
	InvalidParameter(HttpStatus.BAD_REQUEST, 28), // NOSONAR
	/**
	 * Returned when there is no registered handler for a specific HTTP request.
	 */
	@Doc("Returned when there is no registered handler for a specific HTTP request.")
	ServiceNotFound(HttpStatus.NOT_FOUND, 60), // NOSONAR
	/**
	 * Returned when a service endpoint is not (yet) implemented, therefore unavailable.
	 */
	@Doc("Returned when a service endpoint is not (yet) implemented, therefore unavailable.")
	NotImplemented(HttpStatus.NOT_IMPLEMENTED, 1051), // NOSONAR


	// ====================================================================================
	// === others
	// ====================================================================================
	/**
	 * Returned when a requested resource doesn't exist.
	 */
	@Doc("Returned when a requested resource doesn't exist.")
	ResourceNotFound(HttpStatus.NOT_FOUND, 60), // NOSONAR
	/**
	 * Returned when a conflict is detected on the requested resource (already exists).
	 */
	@Doc("Returned when a conflict is detected on the requested resource (e.g. trying to create a resource that already exists).")
	ResourceConflict(HttpStatus.CONFLICT, 1091), // NOSONAR
	/**
	 * Returned when the authenticated principal is not authorized to access the requested resource.
	 */
	@Doc("Returned when the authenticated principal is not authorized to access a requested resource.")
	ResourceAccessDenied(HttpStatus.FORBIDDEN, 50) // NOSONAR
	;

	private final HttpStatus status;
	private final int code;

	ErrorCode(HttpStatus status, int code) {
		this.status = status;
		this.code = code;
	}

	/**
	 * Returns Liveobjects status code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Returns corresponding Http status
	 */
	public HttpStatus getStatus() {
		return status;
	}

	@Target({ ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Doc {
		/**
		 * Documentation
		 */
		String value();
	}
}
