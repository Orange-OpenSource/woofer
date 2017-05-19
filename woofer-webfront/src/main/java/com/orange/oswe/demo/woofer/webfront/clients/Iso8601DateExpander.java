/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront.clients;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import feign.Param.Expander;

public class Iso8601DateExpander implements Expander {
	private static final ISO8601DateFormat FORMAT = new ISO8601DateFormat();
	@Override
	public String expand(Object value) {
		return FORMAT.format(value);
	}
}
