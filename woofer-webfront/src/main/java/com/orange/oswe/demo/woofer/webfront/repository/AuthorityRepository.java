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

import com.orange.oswe.demo.woofer.webfront.domain.Authority;

/**
 * Repository class for <code>Authority</code> domain objects All method names
 * are compliant with Spring Data naming conventions so this interface can
 * easily be extended for Spring Data See here:
 * http://static.springsource.org/spring-data/jpa/docs/current/reference/html/jpa.repositories.html#jpa.query-methods.query-creation
 */
public interface AuthorityRepository extends CrudRepository<Authority, Long> {

	/**
	 * Retrieve <code>Authority</code>s from the data store by last name, returning
	 * all authorities whose last name <i>starts</i> with the given name.
	 *
	 * @param authority
	 *			Value to search for
	 * @return a <code>Collection</code> of matching <code>Authority</code>s (or an
	 *		 empty <code>Collection</code> if none found)
	 */
	Authority findByAuthority(String authority) throws DataAccessException;
}
