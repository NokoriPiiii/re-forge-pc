package com.reforgepc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/account")
    public String account(Model model) {
        model.addAttribute("page", "account");
        return "account/account";
    }

    @GetMapping("/account/profile")
    public String profile(Model model) {
        model.addAttribute("page", "profile");
        model.addAttribute("view", "profile");
        return "account/account";
    }

    @GetMapping("/account/orders")
    public String orders(Model model) {
        model.addAttribute("page", "orders");
        model.addAttribute("view", "orders");
        return "account/account";
    }

    @GetMapping("/account/change-password")
    public String changePassword(Model model) {
        model.addAttribute("page", "change_password");
        model.addAttribute("view", "change-password");
        return "account/account";
    }
}