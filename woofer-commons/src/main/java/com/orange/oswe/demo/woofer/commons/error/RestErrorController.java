/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.commons.error;

import net.logstash.logback.stacktrace.StackHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Deque;

/**
 * Generic error controller handling Rest errors
 * <p>
 * Renders any {@link Exception} into a readable JSON body
 */
@Controller
@RequestMapping(RestErrorController.PATH)
@Order(Ordered.HIGHEST_PRECEDENCE)
//@ControllerAdvice
public class RestErrorController implements ErrorController, HandlerExceptionResolver, Ordered {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected static final String ERROR_ATTRIBUTE = RestErrorController.class.getName()+".error";

    protected static final String ERROR_UNIQUE_ID_HEADER = "X-Error-Uid";

	protected static final String PATH = "/error";

	protected final StackHasher hasher;

	/**
	 * Constructor
	 * @param hasher hasher to use to compute internal error hash
	 */
	public RestErrorController(StackHasher hasher) {
		this.hasher = hasher;
	}

	@Override
	public String getErrorPath() {
		return PATH;
	}

	/**
	 * JSON error handling (default)
	 */
	@RequestMapping
	public ResponseEntity<JsonError[]> errorAsJson(HttpServletRequest request, HttpServletResponse response) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JsonError error = buildError(request);
		handleInternalError(request, response, error);
		return new ResponseEntity<>(new JsonError[] { error }, headers, error.getErrorCode().getStatus());
	}

	/**
	 * Retrieves the current {@link Throwable error} being handled in the {@link HttpServletRequest request}
	 * @param request request
	 * @return error, if any
	 */
	protected Throwable getError(HttpServletRequest request) {
		// first try to get an exception handled by this
		Throwable throwable = (Throwable) request.getAttribute(ERROR_ATTRIBUTE);
		if(throwable == null) {
			// else try to retrieve exception handled by JEE
			throwable = (Throwable)request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
		}
		return throwable;
	}

    /**
     * Builds a {@link JsonError} object representing the error being currently handled in the {@link HttpServletRequest request}
     * @param request request
     * @return error
     */
	protected JsonError buildError(HttpServletRequest request) {
		// either an exception or a status code
		Throwable throwable = getError(request);
		if (throwable != null) {
			return ErrorTranslator.build(throwable);
		}
		// no exception in the request: build JsonError from other JEE attributes (status, message, ...)
		Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		if (statusCode != null) {
			ErrorCode status = ErrorTranslator.getDefaultCode(HttpStatus.valueOf(statusCode));
			String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
			return new JsonError(status, message);
		} else {
			// no error ?!?
			return new JsonError(ErrorCode.Success, "Hey! looks like there is no error...");
		}
	}

	/**
	 * If the given error is internal ({@code 5XX}), then this method:
	 * <ol>
	 *     <li>generated a unique ID,</li>
	 *     <li>adds this ID as a response header,</li>
	 *     <li>replaces the {@link JsonError error} description with a generic message,</li>
	 *     <li>logs the Exception in ERROR with the generated ID (for traceability purpose).</li>
	 * </ol>
	 */
	protected void handleInternalError(HttpServletRequest request, HttpServletResponse response, JsonError error) {
		HttpStatus status = error.getErrorCode().getStatus();
		if(!status.is5xxServerError()) {
			return;
		}
		Throwable throwable = getError(request);
		if(throwable == null) {
			return;
		}

		// dump exception
		// compute request url
		String reqUrl = request.getRequestURI() + (request.getQueryString() == null ? "" : request.getQueryString());
		String fwdUri = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
		String fwdQuery = (String) request.getAttribute(RequestDispatcher.FORWARD_QUERY_STRING);
		if (fwdUri != null) {
			// request was forwarded
			reqUrl = fwdUri + (fwdQuery == null ? "" : "?" + fwdQuery);
		}
		String errUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
		if (errUri != null) {
			// JEE error
			reqUrl = errUri;
		}
		// add an error hash to returned headers and message to retrieve easily from logs
		Deque<String> errHashes = hasher.hexHashes(throwable);
		String topHash = errHashes.peek();

		// add an error hash to returned headers and message to retrieve easily from logs
		String errID = topHash+"-"+Long.toHexString(System.nanoTime());

		// root cause first, topmost error last
		response.setHeader(ERROR_UNIQUE_ID_HEADER, errID);
		// do not return internal exception message (too technical)
		error.setDescription("Internal error [#" + errID + "] occurred in request '" + request.getMethod() + " " + reqUrl + "'");

		// then log error with complete stack for analysis/debugging
		logger.error("Internal error [#{}] occurred in request '{} {}'", errID, request.getMethod(), reqUrl, throwable);
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	/*
	 * HandlerExceptionResolver impl: stores the exception in the request for later use, and forwards to this error controller
	 */
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		// stores the exception in the request attributes
		request.setAttribute(ERROR_ATTRIBUTE, ex);
		// forward to this
		return new ModelAndView("forward:"+PATH);
	}
}
