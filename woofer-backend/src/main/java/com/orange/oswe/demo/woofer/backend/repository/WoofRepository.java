/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.backend.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.orange.oswe.demo.woofer.backend.domain.User;
import com.orange.oswe.demo.woofer.backend.domain.Woof;

/**
 * Repository class for <code>Woof</code> domain objects
 */
@RepositoryRestResource
public interface WoofRepository extends PagingAndSortingRepository<Woof, Long> {
	/**
	 * Returns all {@link Woof woofs} written by the given {@link User author}
	 * <p>
	 * Exposed API is: /api/woofs/search/byAuthor?author=/users/bpitt&page=0&size=20&sort=datetime,desc
	 */
	@RestResource(path="byAuthor", rel="byAuthor")
	Page<Woof> findByAuthor(@Param("author") User author, Pageable pageable) throws DataAccessException;

	Long countByAuthor(@Param("author") User author) throws DataAccessException;

	/**
	 * Returns all {@link Woof woofs} written by the given user, or written by author followed by the given {@link User author}
	 * <p>
	 * Exposed API is: /api/woofs/search/byFollower?follower=/users/bpitt&projection=inlineAuthor&page=0&size=20&sort=datetime,desc
	 */
	@RestResource(path="byFollower", rel="byFollower")
	@Query("SELECT woof FROM com.orange.oswe.demo.woofer.backend.domain.Woof woof WHERE woof.author = :follower OR woof.author IN (SELECT user FROM com.orange.oswe.demo.woofer.backend.domain.User user INNER JOIN user.followers fw WHERE fw = :follower)")
	Page<Woof> findByFollower(@Param("follower") User follower, Pageable pageable) throws DataAccessException;
}
