/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront;

import com.orange.common.logging.web.PrincipalFilter;
import com.orange.common.logging.web.SessionIdFilter;
import com.orange.oswe.demo.woofer.commons.error.RestAndHtmlErrorHandler;
import com.orange.oswe.demo.woofer.commons.error.RestErrorDecoder;
import com.orange.oswe.demo.woofer.commons.filters.SleuthTraceCaptorFilter;
import com.orange.oswe.demo.woofer.commons.tomcat.TomcatCustomizerForLogback;
import feign.Contract;
import feign.codec.ErrorDecoder;
import net.logstash.logback.stacktrace.StackElementFilter;
import net.logstash.logback.stacktrace.StackHasher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter;
import org.springframework.boot.actuate.metrics.jmx.JmxMetricWriter;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.Filter;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
	public EmbeddedServletContainerCustomizer containerCustomizer(@Value("${spring.application.name}") String applicationName, @Value("${custom.access_log.config}") String accessConfigFile) {
		return new TomcatCustomizerForLogback(accessConfigFile, Collections.singletonMap("app.name", applicationName));
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
	 * Override Feign {@link ErrorDecoder}
	 */
	@Bean
	public ErrorDecoder errorDecoder() {
		return new RestErrorDecoder();
	}

	/**
	 * {@link StackHasher} used in Logback config
	 * @param comaSeparatedPatterns list of coma separated patterns
	 */
	@Bean
	public StackHasher throwableHasher(@Value("${custom.logging.ste_exclusions}") String comaSeparatedPatterns) {
		List<Pattern> excludes = Arrays.stream(comaSeparatedPatterns.split("\\s*\\,\\s*")).map(Pattern::compile).collect(Collectors.toList());
		return new StackHasher(StackElementFilter.byPattern(excludes));
	}

	/**
	 * Override default Spring Boot error controller with ours
	 */
	@Bean
	public RestAndHtmlErrorHandler errorHandler(StackHasher hasher) {
		return new RestAndHtmlErrorHandler(hasher);
	}

	@Bean
	public Filter sleuthTraceCaptorFilter() {
		return new SleuthTraceCaptorFilter();
	}
}
