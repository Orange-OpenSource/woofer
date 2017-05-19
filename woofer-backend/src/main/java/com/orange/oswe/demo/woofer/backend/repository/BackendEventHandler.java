/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.backend.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.core.event.RepositoryEvent;
import org.springframework.stereotype.Component;

import com.orange.oswe.demo.woofer.backend.clients.NotifierClient;
import com.orange.oswe.demo.woofer.backend.domain.UnexpectedErrorSimulator;
import com.orange.oswe.demo.woofer.backend.domain.UnexpectedSlownessSimulator;
import com.orange.oswe.demo.woofer.backend.domain.Woof;
import com.orange.oswe.demo.woofer.commons.utils.ToStringLimiter;

/**
 * Component that intercepts {@link RepositoryEvent} to add behavior
 */
@Component
@RepositoryEventHandler
public class BackendEventHandler {
	private static final Logger logger = LoggerFactory.getLogger(BackendEventHandler.class);

	@Autowired
	private CounterService counterService;

	@Autowired
	private NotifierClient notifierClient;
	
	@Autowired
	private UnexpectedSlownessSimulator unexpectedSlownessSimulator;
	
	@Autowired
	private UnexpectedErrorSimulator unexpectedErrorSimulator;

	@HandleBeforeCreate
	public void beforeCreateWoof(Woof woof) {
		logger.debug("posting woof from {}: \"{}\"", woof.getAuthor(), new ToStringLimiter(woof.getMessage()));
		// simulate slowness
		unexpectedSlownessSimulator.maybeSlow(woof.getMessage());
		// simulate errors
		unexpectedErrorSimulator.maybeThrow("woof content", woof.getMessage());
	}

	@HandleAfterCreate
	public void afterCreateWoof(Woof woof) {
		logger.debug("posted woof from {}: \"{}\"", woof.getAuthor(), new ToStringLimiter(woof.getMessage()));
		// increment counter
		counterService.increment("meter.woofs.count");
		// call notification service (async)
		notifierClient.posted(woof.getAuthor().getId(), woof.getMessage());
	}
	
	// TODO: cannot get exactly the added/removed follower
//	@HandleBeforeLinkSave
//	public void beforeAddFollower(User user, Set<User> followers) {
//		logger.debug("adding follower to {}\n\tActual: {}\n\tNew: {}", user, user.getFollowers(), followers);
//	}
//
//	@HandleAfterLinkSave
//	public void afterAddFollower(User user, Set<User> followers) {
//		logger.debug("added follower to {}\n\tActual: {}\n\tNew: {}", user, user.getFollowers(), followers);
//	}
//
//	@HandleBeforeLinkDelete
//	public void beforeRemoveFollower(User user, Set<User> followers) {
//		logger.debug("removing follower from {}\n\tActual: {}\n\tNew: {}", user, user.getFollowers(), followers);
//	}
//
//	@HandleAfterLinkDelete
//	public void afterRemoveFollower(User user, Set<User> followers) {
//		logger.debug("removed follower from {}\n\tActual: {}\n\tNew: {}", user, user.getFollowers(), followers);
//	}

}
