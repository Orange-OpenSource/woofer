package com.orange.oswe.demo.woofer.webfront.mvc;

import com.orange.oswe.demo.woofer.commons.error.ErrorCode;
import com.orange.oswe.demo.woofer.commons.error.JsonError;
import com.orange.oswe.demo.woofer.commons.error.RestErrorController;
import net.logstash.logback.stacktrace.StackHasher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * error controller handling both rest and html clients
 */
@Controller
public class HtmlAndRestErrorController extends RestErrorController {

	/**
	 * Constructor
	 *
	 * @param hasher hasher to use to compute internal error hash
	 */
	public HtmlAndRestErrorController(StackHasher hasher) {
		super(hasher);
	}

	/**
	 * Html error handling
	 */
	@RequestMapping(produces = { MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_XHTML_XML_VALUE })
	public ModelAndView errorAsHtml(HttpServletRequest request, HttpServletResponse response) {
		JsonError error = buildError(request);
        handleInternalError(request, response, error);

		// retrieve error description from annotation
		String description = error.getErrorCode().name();
		try {
			ErrorCode.Doc doc = ErrorCode.class.getField(error.getErrorCode().name()).getAnnotation(ErrorCode.Doc.class);
			if (doc != null) {
				description = doc.value();
			}
		} catch (NoSuchFieldException | SecurityException e) {
			logger.warn("Parsing error while parsing exception", e);
		}

		HttpStatus httpStatus = error.getErrorCode().getStatus();
		response.setStatus(httpStatus.value());

		ModelAndView errorView = new ModelAndView("error");
		errorView.addObject("error", error);
		errorView.addObject("niceHttpStatus", cc(httpStatus.name()));
		errorView.addObject("description", description);

		return errorView;
	}

    /**
     * Turns an uppercase string with underscore separators into Camel Case
     */
	private static String cc(String name) {
        return Arrays.stream(name.split("_")).map(HtmlAndRestErrorController::capitalize).collect(Collectors.joining(" "));
	}

	private static String capitalize(String word) {
	    if(word == null || word.isEmpty()) {
	        return word;
        }
        if(word.length() == 1) {
	        return word.toUpperCase();
        }
        return Character.toUpperCase(word.charAt(0)) + (word.substring(1).toLowerCase());
    }

}
