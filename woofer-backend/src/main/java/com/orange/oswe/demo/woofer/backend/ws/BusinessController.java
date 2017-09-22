package com.orange.oswe.demo.woofer.backend.ws;

import java.util.Date;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Splitter;
import com.orange.oswe.demo.woofer.backend.clients.NotifierClient;
import com.orange.oswe.demo.woofer.backend.domain.UnexpectedErrorSimulator;
import com.orange.oswe.demo.woofer.backend.domain.User;
import com.orange.oswe.demo.woofer.backend.domain.Woof;
import com.orange.oswe.demo.woofer.backend.repository.UserRepository;
import com.orange.oswe.demo.woofer.backend.repository.WoofRepository;
import com.orange.oswe.demo.woofer.commons.utils.ToStringLimiter;

/**
 * Reimplements part of the APIs that involve buisiness logic (event-based)
 */
@RestController
@RequestMapping(path = "/api/business")
public class BusinessController {
	private static final Logger logger = LoggerFactory.getLogger(BusinessController.class);

	@Autowired
	private CounterService counterService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WoofRepository woofRepository;

	@Autowired
	private NotifierClient notifierClient;
	
	@Autowired
	private UnexpectedErrorSimulator unexpectedErrorSimulator;

	@RequestMapping(path = "/users/{userId}/followers", method = RequestMethod.POST, consumes = "text/uri-list")
	@ResponseStatus(HttpStatus.OK)
	public void subscribe(@PathVariable("userId") String userId, @RequestBody String followerUris) throws UserNotFound {
		logger.info("subscribe <{}> to <{}> woofs", followerUris, userId);
		User user = userRepository.findOne(userId);
		if (user == null) {
			throw new UserNotFound("Followed user [" + userId + "] not found");
		}
		for (String followerUri : Splitter.on('\n').split(followerUris)) {
			String followerId = followerUri.substring(followerUri.lastIndexOf('/') + 1);
			User follower = findById(user.getFollowers(), followerId);
			if(follower == null) {
				follower = userRepository.findOne(followerId);
				if (follower == null) {
					throw new UserNotFound("Follower user [" + followerId + "] not found");
				}
				user.getFollowers().add(follower);
			}
		}
		userRepository.save(user);

		// increment counter
		counterService.increment("meter.subscribe.count");

		// call notification service (async)
		for (String followerUri : Splitter.on('\n').split(followerUris)) {
			String followerId = followerUri.substring(followerUri.lastIndexOf('/') + 1);
			notifierClient.subscribed(userId, followerId);
		}
	}
	

	@RequestMapping(path = "/users/{userId}/followers/{followerId}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void unsubscribe(@PathVariable("userId") String userId, @PathVariable("followerId") String followerId) throws UserNotFound {
		logger.info("unsubscribe <{}> from <{}> woofs", followerId, userId);
		User user = userRepository.findOne(userId);
		if (user == null) {
			throw new UserNotFound("Followed user [" + userId + "] not found");
		}
		User follower = findById(user.getFollowers(), followerId);
		if(follower != null) {
			user.getFollowers().remove(follower);
			userRepository.save(user);

			// increment counter
			counterService.increment("meter.unsubscribe.count");

			// call notification service (async)
			notifierClient.unsubscribed(userId, followerId);
		}
	}
	
	private static User findById(Set<User> users, String userId) {
		for(User user : users) {
			if(userId.equals(user.getId())) {
				return user;
			}
		}
		return null;
	}

	@RequestMapping(path = "/users/{userId}/woofs", method = RequestMethod.POST, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public void woof(@PathVariable("userId") String userId, @RequestBody String message) throws UserNotFound {
		logger.info("woof from <{}>: \"{}\"", userId, new ToStringLimiter(message));
		User user = userRepository.findOne(userId);
		if (user == null) {
			throw new UserNotFound("Woof author [" + userId + "] not found");
		}
		// simulate errors
		unexpectedErrorSimulator.maybeThrow("woof content", message);

		woofRepository.save(new Woof(user, new Date(), message));

		// call notification service (async)
		notifierClient.posted(userId, message);
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	static class UserNotFound extends Exception {
		public UserNotFound(String message) {
			super(message);
		}
	}
}
