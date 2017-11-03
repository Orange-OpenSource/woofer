/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.backend.domain;

import org.springframework.stereotype.Component;

/**
 * Fake component that simulates technical errors in the Woofer code
 */
@Component
public class UnexpectedErrorSimulator {
	public void maybeThrow(String thing, String content) {
		if(content.contains("err:back")) {
			throw new IllegalArgumentException("'"+thing+"' caused an unexpected error!");
		}
	}
}
