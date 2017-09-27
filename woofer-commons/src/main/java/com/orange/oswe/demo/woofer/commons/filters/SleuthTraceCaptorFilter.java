package com.orange.oswe.demo.woofer.commons.filters;

import org.slf4j.MDC;

import javax.servlet.*;
import java.io.IOException;

/**
 * Servlet filter that grabs the current Sleuth trace ID and stores it as a request attribute
 * (to be able to log it in the access logs)
 */
public class SleuthTraceCaptorFilter implements Filter {
    public static final String TRACE_ID_NAME = "X-B3-TraceId";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // retrieve current trace ID from MDC
        servletRequest.setAttribute(TRACE_ID_NAME, MDC.get(TRACE_ID_NAME));
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
