/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront.mvc;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class WoofForm {

	@NotEmpty
	@Size(min = 1, max = 140, message = "must be between 1 and 140 characters")
	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
