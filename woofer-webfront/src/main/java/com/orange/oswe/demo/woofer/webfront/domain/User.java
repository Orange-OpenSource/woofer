/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront.domain;

import java.util.List;

/**
 * A Woofer User
 */
public class User {

	private String id;

	private String fullname;

	private String email;
	
	private Integer woofsCount;

	private List<User> followers;

	private List<User> followees;
	
	public User() {
	}
	
	public User(String id, String fullname, String email) {
		this.id = id;
		this.fullname = fullname;
		this.email = email;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<User> getFollowers() {
		return followers;
	}

	public void setFollowers(List<User> followers) {
		this.followers = followers;
	}

	public List<User> getFollowees() {
		return followees;
	}

	public void setFollowees(List<User> followees) {
		this.followees = followees;
	}
	
	public Integer getWoofsCount() {
		return woofsCount;
	}
	
	public void setWoofsCount(Integer woofsCount) {
		this.woofsCount = woofsCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", fullname=" + fullname + ", email=" + email + "]";
	}
}
