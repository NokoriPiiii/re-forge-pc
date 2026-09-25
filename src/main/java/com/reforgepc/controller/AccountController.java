package com.reforgepc.controller;

import com.reforgepc.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {

    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/account")
    public String account(
            Model model,
            HttpSession session) {
        addSessionMessagesToModel(session, model);

        model.addAttribute("page", "account");
        return "account/account";
    }

    @GetMapping("/account/profile")
    public String profile(
            Model model,
            HttpSession session) {
        addSessionMessagesToModel(session, model);

        model.addAttribute("page", "profile");
        model.addAttribute("view", "profile");
        return "account/account";
    }

    @GetMapping("/account/orders")
    public String orders(
            Model model,
            HttpSession session) {
        addSessionMessagesToModel(session, model);

        model.addAttribute("page", "orders");
        model.addAttribute("view", "orders");
        return "account/account";
    }

    @GetMapping("/account/change-password")
    public String changePassword(
            Model model,
            HttpSession session) {
        addSessionMessagesToModel(session, model);

        model.addAttribute("page", "change_password");
        model.addAttribute("view", "change-password");
        return "account/account";
    }

    @PostMapping("/account/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Authentication authentication,
            HttpSession session) {
        String email = authentication.getName();

        try {
            userService.changePassword(
                    email,
                    currentPassword,
                    newPassword,
                    confirmPassword);

            session.setAttribute(
                    "successMessage",
                    "Đổi mật khẩu thành công.");

        } catch (IllegalArgumentException e) {
            session.setAttribute(
                    "errorMessage",
                    e.getMessage());
        }

        return "redirect:/account/change-password";
    }

    private void addSessionMessagesToModel(
            HttpSession session,
            Model model) {
        String successMessage = (String) session.getAttribute("successMessage");

        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
            session.removeAttribute("successMessage");
        }

        String errorMessage = (String) session.getAttribute("errorMessage");

        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            session.removeAttribute("errorMessage");
        }
    }

}