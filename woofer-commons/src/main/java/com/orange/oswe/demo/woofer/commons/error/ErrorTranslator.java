/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.commons.error;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

/**
 * Component in charge of translating any technical or functional {@link Exception} into a {@link JsonError}:
 * <ol>
 *   <li>Manages most of Spring errors,
 *   <li>retrieves the http status from a {@link ResponseStatus} annotation (if present in the handled {@link Exception} class),
 *   <li>or else assumes it is an internal error ({@code 500} http status).
 * </ol>
 */
public class ErrorTranslator {

	private final static Logger LOGGER = LoggerFactory.getLogger(ErrorTranslator.class);

	private ErrorTranslator() {
	}

	/**
	 * Translates the given exception into a {@link JsonError}
	 * @param error exception to translate
	 * @return translate error details
	 */
	public static JsonError build(Throwable error) {
		if (error instanceof AuthenticationException) {
			// TODO: several Unauthorized codes here (missing/invalid/expired credentials)
			// TODO native message ???
			return new JsonError(ErrorCode.MissingCredentials, error);
		} else if (error instanceof NoSuchRequestHandlingMethodException) {
			// message is ok
			return new JsonError(ErrorCode.ServiceNotFound, "No matching handler found for request.");
		} else if (error instanceof HttpRequestMethodNotSupportedException) {
			// message is ok
			return new JsonError(ErrorCode.MethodNotSupported, error);
		} else if (error instanceof HttpMediaTypeNotSupportedException) {
			// message is ok
			return new JsonError(ErrorCode.MediaTypeNotSupported, error);
		} else if (error instanceof HttpMediaTypeNotAcceptableException) {
			// message is ok
			return new JsonError(ErrorCode.MediaTypeNotAcceptable, error);
		} else if (error instanceof MissingServletRequestParameterException) {
			// message is ok
			return new JsonError(ErrorCode.MissingParameter, error);
		} else if (error instanceof TypeMismatchException) {
			// need to rewrite message (too technical)
			return buildTypeMismatch((TypeMismatchException) error);
		} else if (error instanceof HttpMessageNotReadableException) {
			// need to rewrite message (too technical)
			if(error.getCause() instanceof JsonMappingException) {
				return buildJsonMapping((JsonMappingException) error.getCause());
			} else {
				return new JsonError(ErrorCode.InvalidRequestBody, "Request body is invalid and could not be read (see documentation).");
			}
		} else if (error instanceof HttpMessageNotWritableException) {
			// this is most probably an internal exception that occurred while JSON serialization
			// need to rewrite message (too technical)
			return new JsonError(ErrorCode.InternalError, "An internal error occurred while serializing the response");
		} else if (error instanceof MethodArgumentNotValidException) {
			// need to rewrite message (too technical)
			String name = ((MethodArgumentNotValidException) error).getParameter().getParameterName();
			return new JsonError(ErrorCode.InvalidParameter, "Parameter '"+name+"' is not valid (see documentation).");
		} else if (error instanceof MissingServletRequestPartException) {
			// message is ok
			return new JsonError(ErrorCode.MissingRequestPart, error);
		} else if (error instanceof BindException) {
			// need to rewrite message (too technical)
			String name = ((BindException) error).getNestedPath();
			return new JsonError(ErrorCode.InvalidParameter, "Parameter '"+name+"' is not valid (see documentation).");
		} else if (error instanceof NoHandlerFoundException) {
			// need to rewrite message (too technical)
			return new JsonError(ErrorCode.ServiceNotFound, "No handler found for "+(((NoHandlerFoundException) error).getHttpMethod())+" "+(((NoHandlerFoundException) error).getRequestURL()));
		} else {
			// default
			ErrorCode code = getDefaultCode(findStatusByAnnotation(error.getClass()));
			return new JsonError(code, error);
		}
	}

	private static JsonError buildTypeMismatch(TypeMismatchException tme) {
		StringBuilder msg = new StringBuilder();
		if(tme.getPropertyName() == null) {
            msg.append("A parameter");
        } else {
            msg.append("Parameter '").append(tme.getPropertyName()).append("'");
        }
		msg.append(" with value <").append(String.valueOf(tme.getValue())).append(">");
		if(tme.getRequiredType() == null) {
            msg.append(" is not valid (see documentation).");
        } else {
            msg.append(" is not a valid <").append(tme.getRequiredType().getSimpleName()).append("> (see documentation).");
        }
		return new JsonError(ErrorCode.InvalidParameter, msg.toString());
	}

	private static JsonError buildJsonMapping(JsonMappingException jme) {
		StringBuilder locationHint = new StringBuilder();
		JsonLocation location = jme.getLocation();
		if(location != null) {
            locationHint.append(" at line#").append(location.getLineNr()).append(", col#")
            .append(location.getColumnNr());
            if(location.getCharOffset() >= 0) {
                locationHint.append(" (char#").append(location.getCharOffset()).append(")");
            }
        }
		List<Reference> path = jme.getPath();
		if(path != null) {
            locationHint.append(" on <root>");
            for(Reference ref : path) {
                if(ref.getFieldName() == null) {
                    locationHint.append("[").append(ref.getIndex()).append("]");
                } else {
                    locationHint.append(".").append(ref.getFieldName());
                }
            }
        }
		if(jme instanceof UnrecognizedPropertyException) {
            UnrecognizedPropertyException upe = (UnrecognizedPropertyException)jme;
            return new JsonError(ErrorCode.InvalidRequestBody, "Invalid request body"+locationHint.toString()+": unrecognized property '"+(upe.getPropertyName())+"' (see documentation).");
        } else if(jme instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException)jme;
            Class<?> type = ife.getTargetType();
            return new JsonError(ErrorCode.InvalidRequestBody, "Invalid request body"+locationHint.toString()+": value <"+ife.getValue()+"> "+(type == null ? "is invalid" : "is not a valid <"+type.getSimpleName()+">")+" (see documentation).");
        } else {
            return new JsonError(ErrorCode.InvalidRequestBody, "Invalid request body"+locationHint.toString()+" (see documentation).");
        }
	}

	private static final Map<Class<? extends Throwable>, HttpStatus> type2Status = new HashMap<>();

	/**
	 * Looks for {@link ResponseStatus} annotation in the exception type
	 * hierarchy, and determines the {@link HttpStatus}
	 * <p>
	 * If no such annotation is found, the status defaults to
	 * {@link HttpStatus#INTERNAL_SERVER_ERROR}
	 *
	 * @param errorType
	 *            error type to determine HttpStatus
	 * @return HttpStatus corresponding to the given exception type
	 */
	private static HttpStatus findStatusByAnnotation(Class<? extends Throwable> errorType) {
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
	public static ErrorCode getDefaultCode(HttpStatus status) {
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
			LOGGER.error("Unexpected HTTP error code through Spring annotations: " + status);
			return ErrorCode.InternalError;
		}
	}
}
