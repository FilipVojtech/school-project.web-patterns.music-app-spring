package ie.groupproject.musicapp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Locale;

/**
 * @author Filip Vojtěch
 */
@ControllerAdvice
@Slf4j
public class ExceptionHandler {
    private final HttpServletResponse response;
    private final HttpServletRequest request;
    private final MessageSource messageSource;

    public ExceptionHandler(HttpServletResponse response, HttpServletRequest request, MessageSource messageSource) {
        this.response = response;
        this.request = request;
        this.messageSource = messageSource;
    }

    /**
     * Page not found handler.
     * Returns a 404 with information that a page could not be found.
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(value = NoResourceFoundException.class)
    public String pageNotFoundHandler(Model model, Locale locale) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        model.addAttribute("errorType", messageSource.getMessage("error.404", null, locale));
        model.addAttribute("error", messageSource.getMessage("error.404Message", null, locale));
        model.addAttribute("code", "404");
        log.error("Requested resource was not found on '{}'", request.getRequestURL());
        return "pages/error";
    }

    /**
     * Null Pointer Exception handler.
     * Returns generic code 500.
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(value = NullPointerException.class)
    public String nullPointerHandler(Model model, Locale locale) {
        log.error("Null pointer exception on '{}'", request.getRequestURL());
        return return500(model, locale);
    }

    /**
     * Other exception handlers.
     * Returns generic code 500.
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
    public String allOtherExceptionHandler(Model model, Exception ex, Locale locale) {
        log.error("An exception happened at '{}' with message '{}'\n{}", request.getRequestURL(), ex.getMessage(), ex.getStackTrace());
        return return500(model, locale);
    }

    /**
     * Generic code 500 message
     */
    private String return500(Model model, Locale locale) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        model.addAttribute("errorType", messageSource.getMessage("error.500", null, locale));
        model.addAttribute("error", messageSource.getMessage("error.500Message", null, locale));
        model.addAttribute("code", "500");
        return "pages/error";
    }
}
