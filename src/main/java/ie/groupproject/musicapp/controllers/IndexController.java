package ie.groupproject.musicapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping("/")
    public String rootPage(){

        return "pages/index";
    }

    @GetMapping("/rating")
    public String ratingPage() {
        return "pages/rating";
    }
}
