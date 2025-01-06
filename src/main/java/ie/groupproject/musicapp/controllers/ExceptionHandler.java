package ie.groupproject.musicapp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Locale;

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

    @org.springframework.web.bind.annotation.ExceptionHandler(value = NullPointerException.class)
    public String nullPointerHandler(Model model, Locale locale) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        model.addAttribute("errorType", messageSource.getMessage("error.500", null, locale));
        model.addAttribute("error", messageSource.getMessage("error.500Message", null, locale));
        model.addAttribute("code", "500");
        log.error("Null pointer exception on '{}'", request.getRequestURL());
        return "pages/error";
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = NoResourceFoundException.class)
    public String pageNotFoundHandler(Model model, Locale locale) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        model.addAttribute("errorType", messageSource.getMessage("error.404", null, locale));
        model.addAttribute("error", messageSource.getMessage("error.404Message", null, locale));
        model.addAttribute("code", "404");
        log.error("Requested resource was not found on '{}'", request.getRequestURL());
        return "pages/error";
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
    public String allOtherExceptionHandler(Model model, Exception ex) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        model.addAttribute("errType", ex.getClass());
        model.addAttribute("err", ex.getMessage());
        model.addAttribute("code", "400");
        log.error("An exception happened at '{}'", request.getRequestURL());
        return "pages/error";
    }
}
