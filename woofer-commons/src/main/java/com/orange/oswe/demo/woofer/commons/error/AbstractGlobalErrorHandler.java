package com.orange.oswe.demo.woofer.commons.error;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import net.logstash.logback.stacktrace.StackHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base global error management component that:
 * <ul>
 * <li>globally intercepts any unhandled <a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html#mvc-ann-rest-spring-mvc-exceptions">Spring MVC Exceptions</a> (with {@link ExceptionHandler}),</li>
 * <li>allows local exception handling within {@link Controller controllers} with {@link ExceptionHandler} or {@link org.springframework.web.servlet.HandlerExceptionResolver},</li>
 * <li>exposes a {@link ErrorController global exception rendering endpoint},</li>
 * <li>that can be registered at the JEE container level and therefore is able to render non-Spring MVC errors (Spring Security or else),</li>
 * <li>maps any {@link Exception} (Spring MVC or else) into:
 * <ul>
 * <li>an {@link HttpStatus},</li>
 * <li>an {@link ErrorCode error code},</li>
 * <li>a human-understandable error description.</li>
 * </ul>
 * </li>
 * <li>performs specific treatment on <strong>internal errors</strong>:
 * <ul>
 * <li>generates a unique error ID,</li>
 * <li>dumps an error log with the ID and the full error stack trace,</li>
 * <li>replaces the textual description with a generic error message with the ID (for traceability).</li>
 * </ul>
 * </li>
 * </ul>
 * <p>
 * Override it and implement {@link #doRender(HttpServletRequest, HttpServletResponse, ErrorDetails)} to manage final
 * error rendering and possibly content negotiation.
 * <h3>Design references</h3>
 * <ul>
 * <li><a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html#mvc-exceptionhandlers">Handling exceptions in Spring MVC</a></li>
 * <li><a href="http://www.baeldung.com/exception-handling-for-rest-with-spring">Error Handling for REST </a></li>
 * <li><a href="https://stackoverflow.com/questions/23582534/how-to-handle-exceptions-in-spring-mvc-differently-for-html-and-json-requests">This stackoverflow</a> post, with an answer from Dave Syer that confesses that this is the most appropriate design when content negotiation is required.</li>
 * </ul>
 */
@Controller
@RequestMapping(AbstractGlobalErrorHandler.PATH)
public abstract class AbstractGlobalErrorHandler implements ErrorController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected static final String ERROR_ATTRIBUTE = AbstractGlobalErrorHandler.class.getName() + ".error";

    protected static final String ERROR_UNIQUE_ID_HEADER = "X-Error-Uid";

    protected static final String PATH = "/error";

    protected final StackHasher hasher;

    AbstractGlobalErrorHandler(StackHasher hasher) {
        this.hasher = hasher;
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

    /**
     * The only {@link ExceptionHandler} method: catches and stores all <a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html#mvc-ann-rest-spring-mvc-exceptions">Spring MVC Exceptions</a>
     * for later use when the error has to be rendered (as JSON, HTML or else)
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAnyException(HttpServletRequest request, HttpServletResponse response, Exception error) {
        // stores the exception in the request attributes to be reused at render time
        request.setAttribute(ERROR_ATTRIBUTE, error);
        // then directly render the error
        return render(request, response);
    }

    /**
     * The main error rendering endpoint
     * <p>
     * Can be either called directly by this {@link ExceptionHandler}, or by the JEE container (for any non-Spring MVC exception)
     */
    @RequestMapping
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) {
        ErrorDetails details = preRender(request, response);
        return doRender(request, response, details);
    }

    /**
     * Manages error rendering and possibly content negotiation.
     *
     * @param request  request
     * @param response response
     * @param details  error details
     * @return most suitable view (use {@link #findFirstAccept(HttpServletRequest, MediaType...)} for content negotiation)
     */
    protected abstract ModelAndView doRender(HttpServletRequest request, HttpServletResponse response, ErrorDetails details);

    /**
     * Displayable representation of an Exception
     */
    protected static class ErrorDetails {
        final ErrorCode errorCode;
        String description;
        final Exception exception;

        public ErrorDetails(ErrorCode errorCode, String description, Exception exception) {
            this.errorCode = errorCode;
            this.description = description;
            this.exception = exception;
        }

        public ErrorDetails(ErrorCode status, Exception exception) {
            this(status, exception.getMessage(), exception);
        }

        public ErrorDetails(ErrorCode status, String description) {
            this(status, description, null);
        }

        public ErrorCode getErrorCode() {
            return errorCode;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Exception getException() {
            return exception;
        }
    }

    /**
     * This is the global error handling and pre-rendering method.
     * <p>
     *
     * @param request
     * @param response
     * @return error details
     */
    protected ErrorDetails preRender(HttpServletRequest request, HttpServletResponse response) {
        // 1: retrieve exception from request
        Exception error = getErrorFromRequest(request);

        // 2: make error details
        ErrorDetails details = null;
        if (error == null) {
            // this is not a Spring MVC exception and the error controller is being called by the JEE container
            // render error based on other JEE request attributes
            details = makeErrorDetails(request);
        } else {
            details = makeErrorDetails(error);
        }

        // 3: treat internal errors
        if (error != null && details.getErrorCode().getStatus() == HttpStatus.INTERNAL_SERVER_ERROR) {
            processInternalError(request, response, details);
        }

        // 4: set response status
        response.reset();
        response.setStatus(details.getErrorCode().getStatus().value());

        return details;
    }

    /**
     * Retrieves the current {@link Throwable error} being handled in the {@link HttpServletRequest request}
     *
     * @param request request
     * @return error, if any
     */
    protected Exception getErrorFromRequest(HttpServletRequest request) {
        // first try to get an exception handled by this @ExceptionHandler
        Exception error = (Exception) request.getAttribute(ERROR_ATTRIBUTE);
        if (error == null) {
            // else try to retrieve exception handled by JEE container
            error = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        }
        return error;
    }

    /**
     * Makes error details from JEE request attributes
     *
     * @param request request
     * @return error details
     */
    protected ErrorDetails makeErrorDetails(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode != null) {
            ErrorCode code = getDefaultCode(HttpStatus.valueOf(statusCode));
            String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
            return new ErrorDetails(code, message);
        } else {
            // no error ?!?
            logger.warn("Error rendering is being called, but it appears to be no error.");
            return new ErrorDetails(ErrorCode.Success, "Hey! looks like there is no error...");
        }
    }

    /**
     * Makes error details from a genuine {@link Exception}
     * <p>
     * In order:
     * <ol>
     * <li>manages all <a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html#mvc-ann-rest-spring-mvc-exceptions">standard Spring MVC Exceptions</a>,</li>
     * <li>tries to guess Http status by a possible {@link ResponseStatus} annotation on the exception type,</li>
     * <li>else assumes it is an internal error.</li>
     * </ol>
     */
    protected ErrorDetails makeErrorDetails(Exception error) {
        if (error instanceof NoSuchRequestHandlingMethodException) {
            // message is ok
            return new ErrorDetails(ErrorCode.ServiceNotFound, "No matching handler found for request.", error);
        } else if (error instanceof HttpRequestMethodNotSupportedException) {
            // message is ok
            return new ErrorDetails(ErrorCode.MethodNotSupported, error);
        } else if (error instanceof HttpMediaTypeNotSupportedException) {
            // message is ok
            return new ErrorDetails(ErrorCode.MediaTypeNotSupported, error);
        } else if (error instanceof HttpMediaTypeNotAcceptableException) {
            // message is ok
            return new ErrorDetails(ErrorCode.MediaTypeNotAcceptable, error);
        } else if (error instanceof MissingServletRequestParameterException) {
            // message is ok
            return new ErrorDetails(ErrorCode.MissingParameter, error);
        } else if (error instanceof TypeMismatchException) {
            // need to rewrite message (too technical)
            return new ErrorDetails(ErrorCode.InvalidParameter, buildDescription((TypeMismatchException) error), error);
        } else if (error instanceof HttpMessageNotReadableException) {
            // need to rewrite message (too technical)
            if (error.getCause() instanceof JsonMappingException) {
                return new ErrorDetails(ErrorCode.InvalidRequestBody, buildDescription((JsonMappingException) error.getCause()), error);
            } else {
                return new ErrorDetails(ErrorCode.InvalidRequestBody, "Request body is invalid and could not be read (see documentation).", error);
            }
        } else if (error instanceof MethodArgumentNotValidException) {
            // need to rewrite message (too technical)
            String name = ((MethodArgumentNotValidException) error).getParameter().getParameterName();
            return new ErrorDetails(ErrorCode.InvalidParameter, "Parameter '" + name + "' is not valid (see documentation).", error);
        } else if (error instanceof MissingServletRequestPartException) {
            // message is ok
            return new ErrorDetails(ErrorCode.MissingRequestPart, error);
        } else if (error instanceof BindException) {
            // need to rewrite message (too technical)
            String name = ((BindException) error).getNestedPath();
            return new ErrorDetails(ErrorCode.InvalidParameter, "Parameter '" + name + "' is not valid (see documentation).", error);
        } else if (error instanceof NoHandlerFoundException) {
            // need to rewrite message (too technical)
            return new ErrorDetails(ErrorCode.ServiceNotFound, "No handler found for " + (((NoHandlerFoundException) error).getHttpMethod()) + " " + (((NoHandlerFoundException) error).getRequestURL()), error);
//        } else if (error instanceof HttpMessageNotWritableException) {
//        } else if (error instanceof ConversionNotSupportedException) {
//        } else if (error instanceof MissingPathVariableException) {
        } else {
            /*
             * default case:
             * HttpMessageNotWritableException, ConversionNotSupportedException, MissingPathVariableException
             * and others are considered as internal errors
             */
            ErrorCode code = getDefaultCode(findStatusByAnnotation(error.getClass()));
            return new ErrorDetails(code, error);
        }
    }

    /**
     * Processes an internal error:
     * <ol>
     * <li>generates a unique ID,</li>
     * <li>adds this ID to the response headers,</li>
     * <li>replaces the {@link ErrorDetails error} description with a generic message,</li>
     * <li>logs the error with the unique ID and complete stack trace (for traceability purpose).</li>
     * </ol>
     */
    protected void processInternalError(HttpServletRequest request, HttpServletResponse response, ErrorDetails details) {
        // compute request url
        String reqUrl = request.getRequestURI() + (request.getQueryString() == null ? "" : request.getQueryString());
        String fwdUri = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        String fwdQuery = (String) request.getAttribute(RequestDispatcher.FORWARD_QUERY_STRING);
        if (fwdUri != null) {
            // request was forwarded
            reqUrl = fwdUri + (fwdQuery == null ? "" : "?" + fwdQuery);
        }
        String errUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        if (errUri != null) {
            // JEE error
            reqUrl = errUri;
        }

        // generate a unique error ID made of its stack hash + nano time
        String errHash = hasher.hexHash(details.getException());
        String errID = errHash + "-" + Long.toHexString(System.nanoTime());

        // add error ID to response headers
        response.setHeader(ERROR_UNIQUE_ID_HEADER, errID);

        // replace original internal exception description (too technical)
        details.setDescription("Internal error [#" + errID + "] occurred in request '" + request.getMethod() + " " + reqUrl + "'");

        // then log error with its ID and complete stack trace
        logger.error("Internal error [#{}] occurred in request '{} {}'", errID, request.getMethod(), reqUrl, details.getException());
    }

    /**
     * Builds a user understandable description of {@link TypeMismatchException}
     */
    private String buildDescription(TypeMismatchException tme) {
        StringBuilder msg = new StringBuilder();
        if (tme.getPropertyName() == null) {
            msg.append("A parameter");
        } else {
            msg.append("Parameter '").append(tme.getPropertyName()).append("'");
        }
        msg.append(" with value <").append(String.valueOf(tme.getValue())).append(">");
        if (tme.getRequiredType() == null) {
            msg.append(" is not valid (see documentation).");
        } else {
            msg.append(" is not a valid <").append(tme.getRequiredType().getSimpleName()).append("> (see documentation).");
        }
        return msg.toString();
    }

    /**
     * Builds a user understandable description of {@link JsonMappingException}
     */
    private String buildDescription(JsonMappingException jme) {
        StringBuilder locationHint = new StringBuilder();
        JsonLocation location = jme.getLocation();
        if (location != null) {
            locationHint.append(" at line#").append(location.getLineNr()).append(", col#")
                    .append(location.getColumnNr());
            if (location.getCharOffset() >= 0) {
                locationHint.append(" (char#").append(location.getCharOffset()).append(")");
            }
        }
        List<JsonMappingException.Reference> path = jme.getPath();
        if (path != null) {
            locationHint.append(" on <root>");
            for (JsonMappingException.Reference ref : path) {
                if (ref.getFieldName() == null) {
                    locationHint.append("[").append(ref.getIndex()).append("]");
                } else {
                    locationHint.append(".").append(ref.getFieldName());
                }
            }
        }
        if (jme instanceof UnrecognizedPropertyException) {
            UnrecognizedPropertyException upe = (UnrecognizedPropertyException) jme;
            return "Invalid request body" + locationHint.toString() + ": unrecognized property '" + (upe.getPropertyName()) + "' (see documentation).";
        } else if (jme instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) jme;
            Class<?> type = ife.getTargetType();
            return "Invalid request body" + locationHint.toString() + ": value <" + ife.getValue() + "> " + (type == null ? "is invalid" : "is not a valid <" + type.getSimpleName() + ">") + " (see documentation).";
        } else {
            return "Invalid request body" + locationHint.toString() + " (see documentation).";
        }
    }

    private final Map<Class<? extends Throwable>, HttpStatus> type2Status = new HashMap<>();

    /**
     * Looks for {@link ResponseStatus} annotation in the exception type
     * hierarchy, and determines the {@link HttpStatus}
     * <p>
     * If no such annotation is found, the state defaults to
     * {@link HttpStatus#INTERNAL_SERVER_ERROR}
     *
     * @param errorType error type to determine HttpStatus
     * @return HttpStatus corresponding to the given exception type
     */
    protected HttpStatus findStatusByAnnotation(Class<? extends Throwable> errorType) {
        HttpStatus cachedStatus = type2Status.get(errorType);
        if (cachedStatus == null) {
            if (errorType.equals(Throwable.class)) {
                // root exceptions type
                cachedStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            } else {
                // find from Spring annotation
                ResponseStatus st = errorType.getAnnotation(ResponseStatus.class);
                if (st != null) {
                    cachedStatus = st.value();
                } else {
                    // recurse on super type
                    cachedStatus = findStatusByAnnotation((Class<? extends Throwable>) errorType.getSuperclass());
                }
            }
            // add to cache
            type2Status.put(errorType, cachedStatus);
        }
        return cachedStatus;
    }

    /**
     * Retrieves the default {@link ErrorCode} from an {@link HttpStatus}
     */
    protected ErrorCode getDefaultCode(HttpStatus status) {
        if (status == null) {
            return ErrorCode.InternalError;
        }
        switch (status) {
            case UNAUTHORIZED:
                return ErrorCode.MissingCredentials;
            case FORBIDDEN:
                return ErrorCode.ResourceAccessDenied;
            case BAD_REQUEST:
                return ErrorCode.BadRequest;
            case NOT_FOUND:
                return ErrorCode.ResourceNotFound;
            case METHOD_NOT_ALLOWED:
                return ErrorCode.MethodNotSupported;
            case NOT_ACCEPTABLE:
                return ErrorCode.MediaTypeNotAcceptable;
            case UNSUPPORTED_MEDIA_TYPE:
                return ErrorCode.MediaTypeNotSupported;
            case CONFLICT:
                return ErrorCode.ResourceConflict;

            case INTERNAL_SERVER_ERROR:
                return ErrorCode.InternalError;
            case NOT_IMPLEMENTED:
                return ErrorCode.NotImplemented;
            default:
                logger.error("Unexpected HTTP error code through Spring annotations: " + status);
                return ErrorCode.InternalError;
        }
    }

    /**
     * Looks for the first matching {@link MediaType} in the {@code accept} request header
     * <p>
     * Helper method to implement content negotiation
     *
     * @param request        request
     * @param supportedTypes types to test against
     * @return first matching type; or {@code null} if no matching was found
     */
    protected MediaType findFirstAccept(HttpServletRequest request, MediaType... supportedTypes) {
        Enumeration<String> accepts = request.getHeaders("accept");
        while (accepts.hasMoreElements()) {
            List<MediaType> types = MediaType.parseMediaTypes(accepts.nextElement());
            for (MediaType type : types) {
                for (MediaType supported : supportedTypes) {
                    if (type.isCompatibleWith(supported)) {
                        return supported;
                    }
                }
            }
        }
        return null;
    }
}
