package com.example.tripto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LandingController {

    @GetMapping("/")
    public String home() {
        return "public/index"; // points to templates/public/index.html
    }
}
