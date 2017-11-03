/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.backend.repository;

import com.orange.oswe.demo.woofer.backend.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Repository class for <code>User</code> domain objects 
 * <p>
 * Retrieve all users (sorted by name): /api/users?page=0&size=20&sort=fullname,asc
 */
@RepositoryRestResource
public interface UserRepository extends PagingAndSortingRepository<User, String> {
	/**
	 * Retrieve <code>User</code> from the data store by email.
	 * <p>
	 * Exposed API is: /api/users/search/byEmail?email=someone@orange.com
	 *
	 * @param email
	 *			Value to search for
	 * @return a <code>Collection</code> of matching <code>User</code>s (or an
	 *		 empty <code>Collection</code> if none found)
	 */
	@RestResource(path="byEmail", rel="byEmail")
	User findByEmail(@Param("email") String email);
}
