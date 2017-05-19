/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront.domain;

import java.util.Date;

public class Woof {

	private User author;

	private Date datetime = new Date();

	private String message;

	public Woof() {
	}

	public Woof(User author, Date datetime, String message) {
		super();
		this.author = author;
		this.datetime = datetime;
		this.message = message;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User user) {
		this.author = user;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date date) {
		this.datetime = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Woof [author=" + author + ", date=" + datetime + ", message=" + message + "]";
	}

}
