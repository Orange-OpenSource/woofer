/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront.mvc;

import com.orange.oswe.demo.woofer.commons.error.ErrorCode;
import com.orange.oswe.demo.woofer.commons.error.RestErrorHandler;
import net.logstash.logback.stacktrace.StackHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Global error controller that renders errors as JSON or HTML (content negotiation)
 */
@Controller
@ControllerAdvice
public class RestAndHtmlErrorHandler extends RestErrorHandler {

	@Autowired
    public RestAndHtmlErrorHandler(StackHasher hasher) {
        super(hasher);
    }

    @Override
	protected ModelAndView doRender(HttpServletRequest request, HttpServletResponse response, ErrorDetails details) {
		MediaType type = findFirstAccept(request, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML, MediaType.APPLICATION_XHTML_XML);
		if(MediaType.APPLICATION_JSON.equals(type)) {
			return super.doRender(request, response, details);
		} else {
			// render as HTML page
			// retrieve error description from annotation
			String description = details.getErrorCode().name();
			try {
				ErrorCode.Doc doc = ErrorCode.class.getField(details.getErrorCode().name()).getAnnotation(ErrorCode.Doc.class);
				if (doc != null) {
					description = doc.value();
				}
			} catch (NoSuchFieldException | SecurityException e) {
				logger.warn("Parsing error while parsing exception", e);
			}

			HttpStatus httpStatus = details.getErrorCode().getStatus();
			response.setStatus(httpStatus.value());

			ModelAndView htmlView = new ModelAndView("error");
			htmlView.addObject("details", details);
			htmlView.addObject("niceHttpStatus", cc(httpStatus.name()));
			htmlView.addObject("description", description);

			return htmlView;
		}
	}

	/**
	 * Turns an uppercase string with underscore separators into Camel Case
	 */
	private static String cc(String name) {
		return Arrays.stream(name.split("_")).map(RestAndHtmlErrorHandler::capitalize).collect(Collectors.joining(" "));
	}

	private static String capitalize(String word) {
		if(word == null || word.isEmpty()) {
			return word;
		}
		if(word.length() == 1) {
			return word.toUpperCase();
		}
		return Character.toUpperCase(word.charAt(0)) + (word.substring(1).toLowerCase());
	}
}
