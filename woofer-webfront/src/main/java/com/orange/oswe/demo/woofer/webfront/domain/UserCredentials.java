/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * A system user.
 */
@Entity(name = "users")
public class UserCredentials implements Serializable, UserDetails {

	private static final long serialVersionUID = 2002390446280945447L;

	private static final Logger logger = LoggerFactory.getLogger(UserCredentials.class);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;

	@Column(unique = true)
	private String username;

	@Column
	private String password;

	@Column
	private UserStatus status = UserStatus.WORKING;

	@Column
	private boolean enabled = true;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_authorities", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "authority_id"))
	private Set<Authority> authorities;

	public UserCredentials() {
	}

	public UserCredentials(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public Set<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public boolean hasAuthority(String targetAuthority) {
		if (targetAuthority == null) {
			return false;
		}
		if (authorities == null) {
			logger.warn("authorities is null for user {}", this);
		}

		for (Authority authority : authorities) {
			if (targetAuthority.equals(authority.getAuthority())) {
				return true;
			}
		}

		return false;
	}

	public void addAuthority(Authority authority) {
		if (authority == null) {
			return;
		}
		if (authorities == null) {
			logger.warn("authorities is null for user {}", this);
			authorities = new HashSet<>();
		}

		authorities.add(authority);
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean isAccountNonExpired() {
		return status != UserStatus.EXPIRED;
	}

	@Override
	public boolean isAccountNonLocked() {
		return status != UserStatus.LOCKED;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// never expire in our app
		return false;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "UserCredentials [username=" + username + ", status=" + status + ", authorities=" + authorities + "]";
	}
}
