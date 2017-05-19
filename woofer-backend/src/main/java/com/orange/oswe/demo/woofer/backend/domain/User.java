/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.backend.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

/**
 * A Woofer User
 */
@Entity(name="users")
public class User {
	
	/**
	 * {@link User} projection with full information:
	 * <ul>
	 * <li>followers
	 * <li>followees
	 * <li>woofs count
	 * </ul>
	 */
	@Projection(name = "fullInfo", types = { User.class }) 
	interface FullInfo {
		
		String getId();
		
		String getEmail();

		String getFullname();
		
		Set<User> getFollowers();
		
		Set<User> getFollowees();
		
		@Value("#{target.woofs.size()}") 
		int getWoofsCount();
	}
	
	@Id
	private String id;

	@Column
	private String fullname;

	@Column
	private String email;

	@ManyToMany
	@JoinTable(name = "user_followers", 
		joinColumns = { @JoinColumn(name = "user_id", nullable = false) }, 
		inverseJoinColumns = { @JoinColumn(name = "follower_id", nullable = false) })
	private Set<User> followers;
	
	@ManyToMany(mappedBy="followers")
	private Set<User> followees;
	
	@OneToMany(mappedBy="author")
    private Set<Woof> woofs;

	public User() {
	}

	public User(String username, String fullname, String email) {
		this.id = username;
		this.fullname = fullname;
		this.email = email;
	}
	
	/**
	 * Users that follow me
	 */
	public Set<User> getFollowers() {
		return followers;
	}
	
	/**
	 * Users that I am following
	 */
	public Set<User> getFollowees() {
		return followees;
	}

	public String getId() {
		return id;
	}

	public void setId(String username) {
		this.id = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	
	public Set<Woof> getWoofs() {
		return woofs;
	}
	
	public void setWoofs(Set<Woof> woofs) {
		this.woofs = woofs;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", fullname=" + fullname + ", email=" + email + "]";
	}
}
