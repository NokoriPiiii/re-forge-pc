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
            model.addAttribute("errorMessage", "Email hoặc mật khẩu không chính xác.");
        }

        if (registered != null) {
            model.addAttribute("successMessage", "Đăng ký tài khoản thành công. Vui lòng đăng nhập.");
        }

        return "auth/login";
    }
}