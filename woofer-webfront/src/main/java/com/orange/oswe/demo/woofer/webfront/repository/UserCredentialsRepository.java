/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.CrudRepository;

import com.orange.oswe.demo.woofer.webfront.domain.UserCredentials;

/**
 * Repository class for @{UserCredentials} domain objects 
 */
public interface UserCredentialsRepository extends CrudRepository<UserCredentials, Long> {

	/**
	 * Retrieve @{UserCredentials} from the data store by username.
	 *
	 * @param username
	 *			Value to search for
	 * @return a <code>Collection</code> of matching @{UserCredentials} (or an
	 *		 empty <code>Collection</code> if none found)
	 */
	UserCredentials findByUsername(String username) throws DataAccessException;
}
