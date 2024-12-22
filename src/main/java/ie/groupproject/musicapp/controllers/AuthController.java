package ie.groupproject.musicapp.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.LinkedHashMap;

@Controller
@Slf4j
public class AuthController {
    @GetMapping("/login")
    public String loginPage() {
        return "pages/auth/login";
    }

    @PostMapping("/auth/login")
    public RedirectView loginFormHandler() {
        return new RedirectView("/");
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        var map = new LinkedHashMap<Integer, String>();
        for (int i = 0; i < 12; i++) {
            map.put(i + 1, LocalDate.of(2024, i + 1, 1).getMonth().toString());
        }
        model.addAttribute("months", map);
        return "pages/auth/register";
    }

    @PostMapping("/auth/register")
    public RedirectView registerFormHandler() {
        return new RedirectView("/");
    }

    @GetMapping("/auth/logout")
    public RedirectView logoutUser(HttpSession httpSession) {
        httpSession.removeAttribute("user");

        return new RedirectView("/");
    }
}
