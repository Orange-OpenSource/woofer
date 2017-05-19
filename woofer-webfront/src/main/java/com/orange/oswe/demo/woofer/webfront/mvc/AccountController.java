/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront.mvc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.orange.oswe.demo.woofer.webfront.clients.UsersClient;
import com.orange.oswe.demo.woofer.webfront.domain.UnexpectedErrorSimulator;
import com.orange.oswe.demo.woofer.webfront.domain.User;
import com.orange.oswe.demo.woofer.webfront.domain.UserCredentials;
import com.orange.oswe.demo.woofer.webfront.repository.AuthorityRepository;
import com.orange.oswe.demo.woofer.webfront.repository.UserCredentialsRepository;

/**
 * Controller in charge of login, logout and signup
 */
@Controller
public class AccountController {

	private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserCredentialsRepository userRepository;

	@Autowired
	private AuthorityRepository authorityRepository;

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UsersClient usersClient;
	
	@Autowired
	private UnexpectedErrorSimulator unexpectedErrorSimulator;

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public ModelAndView signupView(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelView = new ModelAndView("signup");
		modelView.addObject(new CreateUserForm());
		return modelView;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signup(@Valid CreateUserForm form, BindingResult formBinding, HttpServletRequest request, HttpServletResponse response) throws ServletException {
		logger.debug("signup {}", form);
		if (formBinding.hasErrors()) {
			logger.debug(" ... has errors: {}", formBinding.getAllErrors());
			return "signup";
		}
		if (userRepository.findByUsername(form.getUsername()) != null) {
			logger.debug(" ... username already in use");
			formBinding.addError(new FieldError("createUserForm", "username", "Login already in use"));
			return "signup";
		}
		// verify email is not already used
//		Resource<User> existing = usersClient.findByEmail(form.getEmail());
//		if(existing != null) {
//			logger.debug(" ... email already in use");
//			formBinding.addError(new FieldError("createUserForm", "email", "Email already in use"));
//			return "signup";
//		}

		// simulate errors
		unexpectedErrorSimulator.maybeThrow("username", form.getUsername());
		unexpectedErrorSimulator.maybeThrow("fullname", form.getFullname());
		unexpectedErrorSimulator.maybeThrow("email", form.getEmail());
		unexpectedErrorSimulator.maybeThrow("password", form.getPassword());

		String encodedPwd = passwordEncoder.encode(form.getPassword());
		UserCredentials userCredentials = new UserCredentials(form.getUsername(), encodedPwd);
		userCredentials.addAuthority(authorityRepository.findByAuthority("ROLE_USER"));
		userRepository.save(userCredentials);
		
		// save user
		User user = new User(form.getUsername(), form.getFullname(), form.getEmail());
		usersClient.save(user);
		
		logger.debug(" ... new user created");
		
		// automatically signin
		request.login(form.getUsername(), form.getPassword());

		return "redirect:/";
	}
}
