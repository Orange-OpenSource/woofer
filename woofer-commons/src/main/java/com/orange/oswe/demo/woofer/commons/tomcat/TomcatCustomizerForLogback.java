/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.commons.tomcat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;

import ch.qos.logback.access.tomcat.LogbackValve;

import java.util.Map;

public class TomcatCustomizerForLogback implements EmbeddedServletContainerCustomizer {
	
	private static final Logger logger = LoggerFactory.getLogger(TomcatCustomizerForLogback.class);
	
	private final String accessConfigFile;
	private final Map<String, String> env;

	public TomcatCustomizerForLogback(String accessConfigFile, Map<String, String> env) {
		this.accessConfigFile = accessConfigFile;
		this.env = env;
	}

	@Override
	public void customize(ConfigurableEmbeddedServletContainer factory) {
		logger.info("Installing logback access config ({})...", accessConfigFile);
		if (factory instanceof TomcatEmbeddedServletContainerFactory) {
			TomcatEmbeddedServletContainerFactory containerFactory = (TomcatEmbeddedServletContainerFactory) factory;
			// transfer env (through Java env)
			for(Map.Entry<String, String> entry : env.entrySet()) {
				System.setProperty(entry.getKey(), entry.getValue());
			}
			LogbackValve logbackValve = new LogbackValve();
			logbackValve.setFilename(accessConfigFile);
			containerFactory.addContextValves(logbackValve);
		}
	}
}
