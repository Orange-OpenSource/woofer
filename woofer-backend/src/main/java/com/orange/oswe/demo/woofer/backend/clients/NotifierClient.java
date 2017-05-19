/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.backend.clients;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.scheduling.annotation.Async;

import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * JSON/REST client to notifications APIs 
 */
@FeignClient(name="woofer-notifier", path="/api/notifications")
public interface NotifierClient {
	
	@Async
    @RequestLine("PUT /users/{userId}/followers/{followerId}")
	@Headers("Accept: application/json")
    void subscribed(@Param("userId") String userId, @Param("followerId") String followerId);

	@Async
    @RequestLine("DELETE /users/{userId}/followers/{followerId}")
	@Headers("Accept: application/json")
    void unsubscribed(@Param("userId") String userId, @Param("followerId") String followerId);

	@Async
	@RequestLine("POST /users/{userId}/woofs")
	@Headers({"Content-Type: text/plain", "Accept: application/json"})
	@Body("{message}")
	void posted(@Param("userId") String userId, @Param("message") String message);

}
