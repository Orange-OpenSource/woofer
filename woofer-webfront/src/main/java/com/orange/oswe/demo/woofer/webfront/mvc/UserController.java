/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront.mvc;

import com.orange.oswe.demo.woofer.webfront.clients.BusinessClient;
import com.orange.oswe.demo.woofer.webfront.clients.UsersClient;
import com.orange.oswe.demo.woofer.webfront.clients.WoofsClient;
import com.orange.oswe.demo.woofer.webfront.domain.User;
import com.orange.oswe.demo.woofer.webfront.domain.UserLinkHelper.LinkAction;
import com.orange.oswe.demo.woofer.webfront.domain.Woof;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
 @RequestMapping(value = "/users")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UsersClient usersClient;

	@Autowired
	private WoofsClient woofsClient;
	
	@Autowired
	private BusinessClient businessClient;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView allUsers(Authentication authentication, @RequestParam(name="dir", defaultValue="ASC") Direction direction) {
		ModelAndView modelAndView = new ModelAndView("users");
		Resources<User> users = usersClient.getAll(new Sort(direction, "fullname"));
		modelAndView.addObject("users", users.getContent());
		modelAndView.addObject("direction", direction);
		
		// me
		if(authentication != null && authentication.isAuthenticated()) {
			String meId = authentication.getName();
			User me = usersClient.findByIdWithFullInfo(meId).getContent();
			me.setId(meId);
			modelAndView.addObject("me", me);
		}

		return modelAndView;
	}


	@RequestMapping(value = "/{username}", method = RequestMethod.GET)
	public ModelAndView user(Authentication authentication, @PathVariable("username") String username, @RequestParam(name="pageNumber", required=false, defaultValue="0") int pageNumber) {
		logger.info("user {}", username);
		ModelAndView modelAndView = new ModelAndView("user");
		User user = usersClient.findByIdWithFullInfo(username).getContent();
		user.setId(username);
		modelAndView.addObject("user", user);
		modelAndView.addObject("followersCount", user.getFollowers().size());
		modelAndView.addObject("followeesCount", user.getFollowees().size());
		modelAndView.addObject("woofsCount", user.getWoofsCount());
		
		// me
		if(authentication != null && authentication.isAuthenticated()) {
			String meId = authentication.getName();
			Resource<User> me = usersClient.findByIdWithFullInfo(meId);
			me.getContent().setId(meId);
			modelAndView.addObject("me", me.getContent());
		}

		PagedResources<Woof> woofs = woofsClient.findByAuthor(username, pageNumber, 20);
		modelAndView.addObject("woofs", woofs);
		return modelAndView;
	}
	
	@RequestMapping(value = "/{username}/subscriptions", method = RequestMethod.POST)
	public String subscriptions(HttpServletRequest request, Authentication authentication, @PathVariable("username") String username, @RequestParam("action") LinkAction action) {
		logger.info("subscriptions {}: {}", username, action);
		if(authentication != null && authentication.isAuthenticated()) {
			if(action == LinkAction.SUBSCRIBE) {
				businessClient.addFollower(username, authentication.getName());
			} else {
				businessClient.removeFollower(username, authentication.getName());
			}
		}
		// redirect to referrer page
		String referrer = request.getHeader("referer");
		logger.info(" ... redirect to : {}", referrer);
		return "redirect:"+(referrer == null ? "/" : referrer);
	}

}
