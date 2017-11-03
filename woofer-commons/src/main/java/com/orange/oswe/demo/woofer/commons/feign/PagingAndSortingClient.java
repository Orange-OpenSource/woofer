/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.commons.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * Base interface for {@link FeignClient} implementing CRUD operations with pagination and sorting
 *
 * @param <T>
 *            the domain type the repository manages
 * @param <ID>
 *            the type of the id of the entity the repository manages
 */
public interface PagingAndSortingClient<T, ID> { // NOSONAR
	@RequestLine("POST /")
	@Headers("Content-Type: application/json")
	Resource<T> save(T entity);

	@RequestLine("PUT /{id}")
	@Headers("Content-Type: application/json")
	Resource<T> update(@Param("id") ID id, T entity);

	@RequestLine("GET /{id}")
	@Headers("Content-Type: application/json")
	Resource<T> get(@Param("id") ID id);
	
    @RequestLine("GET /")
	PagedResources<T> getAll();

	@RequestLine("DELETE /{id}")
	@Headers("Content-Type: application/json")
	void delete(@Param("id") ID id);
	
    @RequestLine("GET /?sort={sort}")
	PagedResources<T> getAll(@Param(value="sort", expander=SortExpander.class) Sort sort);
    
    @RequestLine("GET /?page={pageNumber}&size={pageSize}&sort={sort}")
	PagedResources<T> getAll(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize, @Param(value="sort", expander=SortExpander.class) Sort sort);
}
