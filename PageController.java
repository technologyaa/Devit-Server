package com.demo.technologyaa.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";  // src/main/resources/templates/signup.html
    }
}
