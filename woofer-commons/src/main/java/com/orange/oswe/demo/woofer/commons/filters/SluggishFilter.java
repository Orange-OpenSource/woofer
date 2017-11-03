/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.commons.filters;

import javax.servlet.*;
import java.io.IOException;
import java.util.Random;

/**
 * A filter that simulates slow responses
 * <p>
 * Usage:
 * <ul>
 * <li>add <code>?sluggish</code> to any request will pause for 1 sec
 * <li>add <code>?sluggish=VALUE</code> will pause for {@code VALUE} milliseconds
 * <li>add <code>?sluggish=MIN~MAX</code> will pause for a random value between {@code MIN} and {@code MAX} milliseconds
 * </ul>
 */
public class SluggishFilter implements Filter {
	Random random = new Random();
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// no op
	}
	@Override
	public void destroy() {
		// no op
	}
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String sluggish = request.getParameter("sluggish");
		if(sluggish != null) {
			sluggish = sluggish.trim();
			int idx = sluggish.indexOf('~');
			int pause = 1000;
			if(idx < 0) {
				try {
					pause = Integer.parseInt(sluggish);
				} catch(NumberFormatException nfe) {
					// no op
				}
			} else {
				int min = 0;
				int max = 1000;
				try {
					min = Integer.parseInt(sluggish.substring(0, idx));
				} catch(NumberFormatException nfe) {
					// no op
				}
				try {
					max = Integer.parseInt(sluggish.substring(idx+1));
				} catch(NumberFormatException nfe) {
					// no op
				}
				pause = min + random.nextInt(max-min);
			}
			try {
				Thread.sleep(pause);
			} catch (InterruptedException e) { // NOSONAR
			}
		}
		chain.doFilter(request, response);
	}
}
