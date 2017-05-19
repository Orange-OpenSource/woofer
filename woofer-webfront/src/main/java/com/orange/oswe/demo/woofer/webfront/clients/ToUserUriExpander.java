/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront.clients;

import com.orange.oswe.demo.woofer.webfront.domain.User;

import feign.Param.Expander;

public class ToUserUriExpander implements Expander {
	@Override
	public String expand(Object value) {
		if(value instanceof User) {
			return "/users/"+((User)value).getId();
		} else {
			return "/users/"+value;
		}
	}
}
