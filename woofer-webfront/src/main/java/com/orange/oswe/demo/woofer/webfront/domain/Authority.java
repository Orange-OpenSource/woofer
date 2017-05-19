/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;

@Entity(name="authorities")
public class Authority implements GrantedAuthority, Serializable {

	private static final long serialVersionUID = 8635976989721024158L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;
	
	@Column
	@NotEmpty
	private String authority;

	public Authority() {
	}

	public Authority(String authority) {
		this.authority = authority;
	}

	@Override
	public String getAuthority() {
		return authority;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authority == null) ? 0 : authority.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Authority other = (Authority) obj;
		if (authority == null) {
			if (other.authority != null)
				return false;
		} else if (!authority.equals(other.authority))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Authority [" + authority + "]";
	}
}
