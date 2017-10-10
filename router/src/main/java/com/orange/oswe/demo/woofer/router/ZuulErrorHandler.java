package com.orange.oswe.demo.woofer.router;

import com.netflix.zuul.exception.ZuulException;
import com.orange.oswe.demo.woofer.commons.error.ErrorCode;
import com.orange.oswe.demo.woofer.commons.error.RestErrorHandler;
import net.logstash.logback.stacktrace.StackHasher;
import org.springframework.beans.factory.annotation.Autowired;

//@Controller
public class ZuulErrorHandler extends RestErrorHandler {
    @Autowired
    public ZuulErrorHandler(StackHasher hasher) {
        super(hasher);
    }

    @Override
    public ErrorDetails makeErrorDetails(Exception error) {
        if (error instanceof ZuulException) {
            logger.info("handling zuul error {} / {}", ((ZuulException) error).nStatusCode, ((ZuulException) error).errorCause, error);
            return new ErrorDetails(ErrorCode.ServiceUnavailable, error);
        }
        return super.makeErrorDetails(error);
    }
}
