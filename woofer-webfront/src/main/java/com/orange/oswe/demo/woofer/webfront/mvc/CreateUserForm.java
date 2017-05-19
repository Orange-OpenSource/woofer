/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront.mvc;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class CreateUserForm {

	@NotEmpty
	private String username;

	@Email
	@NotEmpty
	private String email;

	@Size(min = 3, message = "must be at least 3 characters")
	private String password;

	@NotEmpty
	private String fullname;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public void setFullname(String fullName) {
		this.fullname = fullName;
	}
	
}
