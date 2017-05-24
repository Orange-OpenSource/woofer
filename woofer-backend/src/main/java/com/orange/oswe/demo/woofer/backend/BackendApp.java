/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.backend;

import com.orange.oswe.demo.woofer.commons.error.JsonErrorDecoder;
import com.orange.oswe.demo.woofer.commons.error.RestErrorController;
import com.orange.oswe.demo.woofer.commons.tomcat.TomcatCustomizerForLogback;
import feign.Contract;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter;
import org.springframework.boot.actuate.metrics.jmx.JmxMetricWriter;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jmx.export.MBeanExporter;

/**
 * The Woofer backend service
 */
@SpringBootApplication
@EnableEurekaClient
@EnableAutoConfiguration
@EnableFeignClients
public class BackendApp {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(BackendApp.class, args);
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

	/**
	 * Override default Spring Boot {@link ErrorController} with ours
	 */
	@Bean
	public ErrorController errorController() {
		return new RestErrorController();
	}

	/**
	 * Override default Feign {@link ErrorDecoder}
	 */
	@Bean
	public ErrorDecoder errorDecoder() {
		return new JsonErrorDecoder();
	}
}
