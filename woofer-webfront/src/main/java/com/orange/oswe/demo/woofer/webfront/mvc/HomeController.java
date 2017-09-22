/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront.mvc;

import java.util.Date;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.hateoas.PagedResources;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.orange.oswe.demo.woofer.webfront.clients.UsersClient;
import com.orange.oswe.demo.woofer.webfront.clients.WoofsClient;
import com.orange.oswe.demo.woofer.webfront.domain.UnexpectedErrorSimulator;
import com.orange.oswe.demo.woofer.webfront.domain.UnexpectedSlownessSimulator;
import com.orange.oswe.demo.woofer.webfront.domain.User;
import com.orange.oswe.demo.woofer.webfront.domain.Woof;

@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private CounterService counterService;

	@Autowired
	private UsersClient usersClient;

	@Autowired
	private WoofsClient woofsClient;
	
	@Autowired
	private UnexpectedSlownessSimulator unexpectedSlownessSimulator;
	
	@Autowired
	private UnexpectedErrorSimulator unexpectedErrorSimulator;

	@RequestMapping("/")
	public ModelAndView home(Authentication authentication, HttpServletRequest req, @RequestParam(name="pageNumber", required=false, defaultValue="0") int pageNumber) {
		if(authentication != null && authentication.isAuthenticated()) {
			logger.info("user home page");
			ModelAndView modelAndView = new ModelAndView("userhome");
			modelAndView.addObject("authentication", authentication);
			String meId = authentication.getName();
			User me = usersClient.findByIdWithFullInfo(meId).getContent();
			me.setId(meId);
			modelAndView.addObject("me", me);
			modelAndView.addObject("followersCount", me.getFollowers().size());
			modelAndView.addObject("followeesCount", me.getFollowees().size());
			modelAndView.addObject("woofsCount", me.getWoofsCount());

			// retrieve woofs for me
			PagedResources<Woof> woofs = woofsClient.findByFollower(meId, pageNumber, 20);
			logger.info("woofs: {}", woofs);
			modelAndView.addObject("woofs", woofs);
			return modelAndView;
		} else {
			logger.info("guest home page");
			return new ModelAndView("guesthome");
		}
	}

	@RequestMapping(value = "/woofs", method = RequestMethod.POST)
	public String post(WoofForm form, BindingResult formBinding, Authentication authentication) {
		logger.info("new woof posted");
		// increment woofs counter
		counterService.increment("meter.woofs.count");
		
		// simulate slowness
		unexpectedSlownessSimulator.maybeSlow(form.getContent());

		// simulate errors
		unexpectedErrorSimulator.maybeThrow("woof content", form.getContent());

		String meId = authentication.getName();

		woofsClient.post(meId, new Date(), form.getContent());
		return "redirect:/";
	}

}
