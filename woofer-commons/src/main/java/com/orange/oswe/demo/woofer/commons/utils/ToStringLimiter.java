/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.commons.utils;

public class ToStringLimiter {
	private final int maxLen;
	private final Object wrapped;

	public ToStringLimiter(int maxLen, Object wrapped) {
		this.maxLen = maxLen;
		this.wrapped = wrapped;
	}
	public ToStringLimiter(Object wrapped) {
		this(50, wrapped);
	}

	public String toString() {
		String str = String.valueOf(wrapped);
		return str.length() > maxLen ? str.substring(0, maxLen) : str;
	}
}
