package com.reforgepc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(
        @RequestParam(required = false) String error,
        @RequestParam(required = false) String registered,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password.");
        }

        if (registered != null) {
            model.addAttribute("success", "Registration successful. Please log in.");
        }

        return "auth/login";
    }
}