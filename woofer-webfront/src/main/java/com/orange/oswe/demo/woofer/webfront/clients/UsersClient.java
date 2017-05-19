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
import org.springframework.hateoas.Resources;

import com.orange.oswe.demo.woofer.commons.feign.PagingAndSortingClient;
import com.orange.oswe.demo.woofer.webfront.domain.User;
import com.orange.oswe.demo.woofer.webfront.domain.Woof;

import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * JSON/REST client to backend users repository APIs 
 */
@FeignClient(name="woofer-backend", path="/api/users")
public interface UsersClient extends PagingAndSortingClient<User, String> {
	
	@RequestLine("GET /{userId}/woofs?sort=datetime,desc")
	Resources<Woof> getWoofs(@Param("userId") String userId);

    @RequestLine("GET /{userId}?projection=fullInfo")
    Resource<User> findByIdWithFullInfo(@Param("userId") String userId);

    @RequestLine("GET /search/byEmail?email={email}")
    Resource<User> findByEmail(@Param("email") String email);

    @RequestLine("POST /{userId}/followers")
    @Headers("Content-Type: text/uri-list")
    @Body("{followerId}")
    void addFollower(@Param("userId") String userId, @Param(value="followerId", expander = ToUserUriExpander.class) String followerId);
    
    @RequestLine("DELETE /{userId}/followers/{followerId}")
    void removeFollower(@Param("userId") String userId, @Param("followerId") String followerId);
}
