/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront.clients;

import com.orange.oswe.demo.woofer.webfront.domain.Woof;
import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;

import java.util.Date;

/**
 * JSON/REST client to backend woofs repository APIs 
 */
@FeignClient(name = "woofer-backend", path = "/backend/api/woofs")
public interface WoofsClient {

	@RequestLine("GET /search/byAuthor?author={userId}&projection=inlineAuthor&page={pageNumber}&size={pageSize}&sort=datetime,desc")
	PagedResources<Woof> findByAuthor(@Param(value = "userId", expander = ToUserUriExpander.class) String userId, @Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize);

	@RequestLine("GET /search/byFollower?follower={userId}&projection=inlineAuthor&page={pageNumber}&size={pageSize}&sort=datetime,desc")
	PagedResources<Woof> findByFollower(@Param(value = "userId", expander = ToUserUriExpander.class) String userId, @Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize);

	@RequestLine("POST /")
	@Headers("Content-Type: application/json")
	// json curly braces must be escaped!
	@Body("%7B\"author\": \"{userId}\", \"datetime\": \"{datetime}\", \"message\": \"{message}\"%7D")
	Resource<Woof> post(@Param(value = "userId", expander = ToUserUriExpander.class) String userId,
			@Param(value = "datetime", expander = Iso8601DateExpander.class) Date datetime, @Param("message") String message);
}
