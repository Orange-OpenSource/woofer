/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Misc controller
 */
@Controller
@RequestMapping(value = "/misc")
public class MiscController {

	@RequestMapping(value = "/err/500", method = RequestMethod.GET)
	public void throwError1(HttpServletRequest request, HttpServletResponse response) {
		throw new IllegalArgumentException("ah, too bad... an error !");
	}

	@RequestMapping(value = "/err/post", method = RequestMethod.POST)
	public String post(HttpServletRequest request, HttpServletResponse response) {
		return "redirect:/";
	}
	
	@RequestMapping(value = "/err/missingParam", method = RequestMethod.GET)
	public String res(@RequestParam("param1") String param1) {
		return "redirect:/";
	}
}
