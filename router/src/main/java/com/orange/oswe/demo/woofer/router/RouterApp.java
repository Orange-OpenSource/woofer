package com.orange.oswe.demo.woofer.router;

import ch.qos.logback.access.tomcat.LogbackValve;
import com.orange.oswe.demo.woofer.commons.filters.SleuthTraceCaptorFilter;
import net.logstash.logback.stacktrace.StackElementFilter;
import net.logstash.logback.stacktrace.StackHasher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
public class RouterApp {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RouterApp.class, args);
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer(@Value("${custom.access_log.config}") String accessConfigFile){
        return configurableEmbeddedServletContainer -> {
            if(configurableEmbeddedServletContainer instanceof TomcatEmbeddedServletContainerFactory){
                TomcatEmbeddedServletContainerFactory containerFactory = (TomcatEmbeddedServletContainerFactory) configurableEmbeddedServletContainer;
                LogbackValve logbackValve = new LogbackValve();
                logbackValve.setFilename(accessConfigFile);
                containerFactory.addContextValves(logbackValve);
            }
        };
    }

    /**
     * {@link StackHasher} used in Logback config (required by {@link org.springframework.boot.autoconfigure.web.ErrorController})
     * @param comaSeparatedPatterns list of coma separated patterns
     */
    @Bean
    public StackHasher throwableHasher(@Value("${custom.logging.ste_exclusions}") String comaSeparatedPatterns) {
        List<Pattern> excludes = Arrays.stream(comaSeparatedPatterns.split("\\s*\\,\\s*")).map(Pattern::compile).collect(Collectors.toList());
        return new StackHasher(StackElementFilter.byPattern(excludes));
    }

    @Bean
    public javax.servlet.Filter sleuthTraceCaptorFilter() {
        return new SleuthTraceCaptorFilter();
    }

}
