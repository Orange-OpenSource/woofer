/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.backend.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.rest.core.config.Projection;

/**
 * A woof (short message)
 */
@Entity(name="woof")
public class Woof {
	
	/**
	 * {@link Woof} projection with inlined author
	 */
	@Projection(name = "inlineAuthor", types = { Woof.class }) 
	interface InlineAuthor {

		User getAuthor();

		Date getDatetime();

		String getMessage();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;

	@ManyToOne
	@JoinColumn(name = "author_id")
	@NotNull
	private User author;

	@Column
	private Date datetime = new Date();

	@Column
	@NotEmpty
	private String message;

	public Woof() {
	}

	public Woof(User user, Date date, String message) {
		this.author = user;
		this.datetime = date;
		this.message = message;
	}

	public Long getId() {
		return id;
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
