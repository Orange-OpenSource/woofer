/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront.domain;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Fake component that simulates slow treatments
 */
@Component
public class UnexpectedSlownessSimulator {
	private static final Logger LOGGER = LoggerFactory.getLogger(UnexpectedSlownessSimulator.class);
	private static final Pattern SLOW_PATTERN = Pattern.compile("slow\\:front(?:\\:(\\d+)(?:~(\\d+))?)?");
	private static final Random RANDOM = new Random();

	public void maybeSlow(String content) {
		Matcher matcher = SLOW_PATTERN.matcher(content);
		if (matcher.find()) {
			int pause = 500;
			if (matcher.group(1) != null) {
				if (matcher.group(2) != null) {
					int min = Integer.parseInt(matcher.group(1));
					int max = Integer.parseInt(matcher.group(2));
					pause = min + RANDOM.nextInt(max - min);
				} else {
					pause = Integer.parseInt(matcher.group(1));
				}
			}
			LOGGER.info("Simulate slow treatment ({}): pause {}ms", matcher.group(), pause);
			try {
				Thread.sleep(pause);
			} catch (InterruptedException e) {
			}
		}
	}
}
