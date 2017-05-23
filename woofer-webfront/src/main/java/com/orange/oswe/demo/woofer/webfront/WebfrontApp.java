/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront;

import java.security.NoSuchAlgorithmException;

import javax.servlet.Filter;

import com.orange.oswe.demo.woofer.commons.error.WooferErrorController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter;
import org.springframework.boot.actuate.metrics.jmx.JmxMetricWriter;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.orange.common.logging.web.PrincipalFilter;
import com.orange.common.logging.web.SessionIdFilter;
import com.orange.oswe.demo.woofer.commons.tomcat.TomcatCustomizerForLogback;

import feign.Contract;

/**
 * The Woofer webfront application
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class WebfrontApp {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(WebfrontApp.class, args);
	}

	/**
	 * Use default Feign annotations instead of Spring one's
	 */
	@Bean
	public Contract feignContract() {
		return new Contract.Default();
	}

	/**
	 * Configures the embedded Tomcat server: installs logback access
	 */
	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer(@Value("${custom.access_log.config}") String accessConfigFile) {
		return new TomcatCustomizerForLogback(accessConfigFile);
	}

	/**
	 * Installs a JMX metrics reporter
	 */
	@Bean
	@ExportMetricWriter
	@Profile({ "jmx" })
	public MetricWriter metricWriter(MBeanExporter exporter) {
		return new JmxMetricWriter(exporter);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Install {@link PrincipalFilter} on every request
	 */
	@Bean
	public Filter principalFilter(@Value("${custom.logging.principal.hash_algo}") String hashAlgorithm) throws NoSuchAlgorithmException {
		PrincipalFilter filter = new PrincipalFilter();
		filter.setHashAlgorithm(hashAlgorithm);
		return filter;
	}

	/**
	 * Install {@link SessionIdFilter} on every request
	 */
	@Bean
	public Filter sessionIdFilter() {
		return new SessionIdFilter();
	}

	/**
	 * Override default Spring Boot {@link ErrorController} with ours
	 */
	@Bean
	public ErrorController errorController() {
		return new WooferErrorController();
	}
}
