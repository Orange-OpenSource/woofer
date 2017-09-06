/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.commons.error;

import net.logstash.logback.stacktrace.StackHasher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Global error controller that renders errors as JSON
 */
@Controller
@ControllerAdvice
public class RestErrorHandler extends AbstractGlobalErrorHandler {

	public RestErrorHandler(StackHasher hasher) {
		super(hasher);
	}

	@Override
	protected ModelAndView doRender(HttpServletRequest request, HttpServletResponse response, ErrorDetails details) {
		// render as JSON
		MappingJackson2JsonView view = new MappingJackson2JsonView();
		view.setExtractValueFromSingleKeyModel(true);
		ModelAndView jsonView = new ModelAndView(view);
		jsonView.addObject(new RestError[]{new RestError(details.getErrorCode(), details.getDescription())});
		return jsonView;
	}
}
