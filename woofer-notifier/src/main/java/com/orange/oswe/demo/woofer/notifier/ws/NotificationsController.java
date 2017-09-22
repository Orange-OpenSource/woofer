/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.notifier.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.orange.oswe.demo.woofer.commons.utils.ToStringLimiter;

/**
 * Notifier endpoints
 */
@RestController
@RequestMapping(path = "/api/notifications")
public class NotificationsController {
	private static final Logger logger = LoggerFactory.getLogger(NotificationsController.class);
	
	@Autowired
	private UnexpectedSlownessSimulator unexpectedSlownessSimulator;

	@Autowired
	private UnexpectedErrorSimulator unexpectedErrorSimulator;

	@RequestMapping(path = "/users/{userId}/followers/{followerId}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void subscribed(@PathVariable("userId") String userId, @PathVariable("followerId") String followerId) {
		logger.info("<{}> subscribe to <{}> woofs", followerId, userId);
	}

	@RequestMapping(path = "/users/{userId}/followers/{followerId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void unsubscribed(@PathVariable("userId") String userId, @PathVariable("followerId") String followerId) {
		logger.info("<{}> unsubscribed to <{}> woofs", followerId, userId);
	}

	@RequestMapping(path = "/users/{userId}/woofs", method = RequestMethod.POST, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public void woof(@PathVariable("userId") String userId, @RequestBody String message) {
		logger.info("woof sent from <{}>: \"{}\"", userId, new ToStringLimiter(message));

		// simulate slowness
		unexpectedSlownessSimulator.maybeSlow(message);

		// simulate errors
		unexpectedErrorSimulator.maybeThrow("woof content", message);
	}
}
