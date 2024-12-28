package ie.groupproject.musicapp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LanguageController {
    @GetMapping("/changeLanguage")
    public String changeLanguage(@RequestParam("lang") String lang, HttpServletRequest request) {
        return "redirect:" + request.getHeader("Referer");
    }
}
