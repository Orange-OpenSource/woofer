/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront.clients;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.Resource;

import com.orange.oswe.demo.woofer.webfront.domain.Woof;

import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * JSON/REST client to backend business APIs 
 */
@FeignClient(name="woofer-backend", path="/backend/api/business")
public interface BusinessClient {
	
    @RequestLine("POST /users/{userId}/followers")
    @Headers("Content-Type: text/uri-list")
    @Body("{followerId}")
    void addFollower(@Param("userId") String userId, @Param(value="followerId", expander = ToUserUriExpander.class) String followerId);
    
    @RequestLine("DELETE /users/{userId}/followers/{followerId}")
    void removeFollower(@Param("userId") String userId, @Param("followerId") String followerId);

	@RequestLine("POST /users/{userId}/woofs")
	@Headers("Content-Type: text/plain")
	@Body("{message}")
	Resource<Woof> post(@Param("userId") String userId, @Param("message") String message);

}
