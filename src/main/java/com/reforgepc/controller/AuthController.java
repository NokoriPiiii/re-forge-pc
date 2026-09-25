package com.reforgepc.controller;

import com.reforgepc.entity.Otp;
import com.reforgepc.entity.OtpPurpose;
import com.reforgepc.repository.UserRepository;
import com.reforgepc.service.EmailService;
import com.reforgepc.service.OtpService;
import com.reforgepc.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private static final long MIN_OTP_REQUEST_TIME_MS = 1500;

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final EmailService emailService;
    private final UserService userService;

    public AuthController(
            UserRepository userRepository,
            OtpService otpService,
            EmailService emailService,
            UserService userService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.emailService = emailService;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String registered,
            HttpSession session,
            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Email hoặc mật khẩu không chính xác.");
        }

        if (registered != null) {
            model.addAttribute("successMessage", "Đăng ký tài khoản thành công. Vui lòng đăng nhập.");
        }

        String successMessage = (String) session.getAttribute("successMessage");

        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
            session.removeAttribute("successMessage");
        }

        return "auth/login";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String requestPasswordReset(
            @RequestParam String email,
            HttpSession session) {
        long startTime = System.currentTimeMillis();

        if (userRepository.existsByEmail(email)) {
            session.setAttribute("resetPasswordEmail", email);

            if (otpService.canRequestOtp(email, OtpPurpose.RESET_PASSWORD)) {
                Otp otp = otpService.generateOtp(
                        email,
                        OtpPurpose.RESET_PASSWORD);

                emailService.sendOtp(
                        email,
                        otp.getRawOtp(),
                        otp.getExpiresAt());
            } else {
                session.setAttribute(
                        "errorMessage",
                        "Vui lòng chờ 60 giây trước khi yêu cầu mã OTP mới.");
            }
        }

        waitForMinimumResponseTime(startTime);

        if (session.getAttribute("errorMessage") == null) {
            session.setAttribute(
                    "successMessage",
                    "Mã OTP đã được gửi đến email của bạn.");
        }

        return "redirect:/forgot-password/verify";
    }

    @GetMapping("/forgot-password/verify")
    public String verifyOtpPage(
            HttpSession session,
            Model model) {
        String errorMessage = (String) session.getAttribute("errorMessage");

        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            session.removeAttribute("errorMessage");
        }

        String successMessage = (String) session.getAttribute("successMessage");

        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
            session.removeAttribute("successMessage");
        }

        return "auth/verify-otp";
    }

    @PostMapping("/forgot-password/resend")
    public String resendOtp(HttpSession session) {
        String email = (String) session.getAttribute("resetPasswordEmail");

        if (email == null) {
            return "redirect:/forgot-password";
        }

        if (!otpService.canRequestOtp(email, OtpPurpose.RESET_PASSWORD)) {
            session.setAttribute(
                    "errorMessage",
                    "Vui lòng chờ 60 giây trước khi yêu cầu mã OTP mới.");

            return "redirect:/forgot-password/verify";
        }

        Otp otp = otpService.generateOtp(
                email,
                OtpPurpose.RESET_PASSWORD);

        emailService.sendOtp(
                email,
                otp.getRawOtp(),
                otp.getExpiresAt());

        session.setAttribute(
                "successMessage",
                "Mã OTP mới đã được gửi đến email của bạn.");

        return "redirect:/forgot-password/verify";
    }

    @PostMapping("/forgot-password/verify")
    public String verifyOtp(
            @RequestParam String otp,
            HttpSession session) {
        String email = (String) session.getAttribute("resetPasswordEmail");

        if (email == null) {
            return "redirect:/forgot-password";
        }

        boolean verified = otpService.verifyOtp(
                email,
                OtpPurpose.RESET_PASSWORD,
                otp);

        if (!verified) {
            session.setAttribute(
                    "errorMessage",
                    "Mã OTP không chính xác hoặc đã hết hạn.");

            return "redirect:/forgot-password/verify";
        }

        session.setAttribute("resetPasswordVerified", true);

        return "redirect:/forgot-password/reset";
    }

    @GetMapping("/forgot-password/reset")
    public String resetPasswordPage(
            HttpSession session,
            Model model) {
        Boolean verified = (Boolean) session.getAttribute("resetPasswordVerified");

        if (!Boolean.TRUE.equals(verified)) {
            return "redirect:/forgot-password";
        }

        String errorMessage = (String) session.getAttribute("errorMessage");

        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            session.removeAttribute("errorMessage");
        }

        return "auth/reset-password";
    }

    @PostMapping("/forgot-password/reset")
    public String resetPassword(
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session) {
        Boolean verified = (Boolean) session.getAttribute("resetPasswordVerified");
        String email = (String) session.getAttribute("resetPasswordEmail");

        if (!Boolean.TRUE.equals(verified) || email == null) {
            return "redirect:/forgot-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            session.setAttribute(
                    "errorMessage",
                    "Mật khẩu xác nhận không khớp.");

            return "redirect:/forgot-password/reset";
        }

        userService.resetPassword(email, newPassword);

        session.removeAttribute("resetPasswordEmail");
        session.removeAttribute("resetPasswordVerified");

        session.setAttribute(
                "successMessage",
                "Đặt lại mật khẩu thành công. Vui lòng đăng nhập.");

        return "redirect:/login";
    }

    private void waitForMinimumResponseTime(long startTime) {
        long elapsed = System.currentTimeMillis() - startTime;
        long remaining = MIN_OTP_REQUEST_TIME_MS - elapsed;

        if (remaining <= 0) {
            return;
        }

        try {
            Thread.sleep(remaining);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}