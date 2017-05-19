/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.commons.error;

import java.io.IOException;
import java.util.Deque;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.orange.common.logging.utils.ErrorSignature;
import com.orange.oswe.demo.woofer.commons.error.ErrorCode.Doc;

/**
 * Global Error controller
 * <p>
 * Renders any {@link Exception} into a view or a readable JSON structure
 */
@Controller
@ControllerAdvice
public class WooferErrorController implements ErrorController {

	private static final Logger LOGGER = LoggerFactory.getLogger(WooferErrorController.class);

	@Override
	public String getErrorPath() {
		return "/error";
	}

	/**
	 * Handles all errors and forwards to itself
	 */
	@ExceptionHandler({ Exception.class, })
	public void handleException(Exception ex, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// set the exception as a request attribute (same as JEE)
		req.setAttribute(RequestDispatcher.ERROR_EXCEPTION, ex);

		// forward to the error page (in cola-front sevlet)
		req.getRequestDispatcher("/error").forward(req, resp);
	}

	/**
	 * Html error handling
	 */
	@RequestMapping(value = "/error", produces = { MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_XHTML_XML_VALUE })
	public ModelAndView errorAsHtml(HttpServletRequest request, HttpServletResponse response) {
		DisplayableError error = getError(request, response);

		// retrieve error description from annotation
		String description = error.getStatus().name();
		try {
			Doc doc = ErrorCode.class.getField(error.getStatus().name()).getAnnotation(Doc.class);
			if (doc != null) {
				description = doc.value();
			}
		} catch (NoSuchFieldException | SecurityException e) {
			LOGGER.debug("Parsing error while parsing exception", e);
		}

		HttpStatus httpStatus = error.getStatus().getStatus();
		response.setStatus(httpStatus.value());

		ModelAndView errorView = new ModelAndView("error");
		errorView.addObject("error", error);
		errorView.addObject("niceHttpStatus", cc(httpStatus.name()));
		errorView.addObject("description", description);

		return errorView;
	}

	/**
	 * JSON error handling
	 */
	@RequestMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DisplayableError[]> errorAsJson(HttpServletRequest request, HttpServletResponse response) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		DisplayableError error = getError(request, response);
		return new ResponseEntity<>(new DisplayableError[] { error }, headers, error.getStatus().getStatus());
	}

	/**
	 * default error handling (status code only)
	 */
	@RequestMapping(value = "/error")
	public ResponseEntity<Void> errorDefault(HttpServletRequest request, HttpServletResponse response) {
		DisplayableError error = getError(request, response);
		return new ResponseEntity<>(error.getStatus().getStatus());
	}

	private DisplayableError getError(HttpServletRequest request, HttpServletResponse response) {
		DisplayableError error;
		// either an exception or a status code
		Throwable throwable = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
		// to prevent Tomcat from handling...
		request.removeAttribute(RequestDispatcher.ERROR_EXCEPTION);
		Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		if (throwable != null) {
			error = ErrorTranslator.build(throwable);
		} else if (statusCode != null) {
			ErrorCode status = ErrorTranslator.getDefaultCode(HttpStatus.valueOf(statusCode));
			String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
			error = new DisplayableError(status, message);
		} else {
			// no error ?!?
			error = new DisplayableError(ErrorCode.Success, "Hey! looks like there is no error...");
		}

		HttpStatus status = error.getStatus().getStatus();
		if (status.is5xxServerError() && throwable != null) {
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
			Deque<String> errHashes = ErrorSignature.hexHashes(throwable);
			String topHash = errHashes.peek();

			// add an error hash to returned headers and message to retrieve easily from logs
			String errID = topHash+"-"+Long.toHexString(System.nanoTime());
			
			// root cause first, topmost error last
			response.setHeader("X-Error-Uid", errID);
			// do not return internal exception message (too technical)
			error.setDescription("Internal error [#" + errID + "] occurred in request '" + request.getMethod() + " " + reqUrl + "'");

			// then log error with complete stack for analysis/debugging
			LOGGER.error("Internal error [#{}] occurred in request '{} {}'", errID, request.getMethod(), reqUrl, throwable);
		}

		return error;
	}

	private String cc(String name) {
		String[] tokens = name.split("_");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tokens.length; i++) {
			if (i > 0) {
				sb.append(' ');
			}
			sb.append(Character.toUpperCase(tokens[i].charAt(0)));
			if (tokens[i].length() > 1) {
				sb.append(tokens[i].substring(1).toLowerCase());
			}
		}
		return sb.toString();
	}
}
