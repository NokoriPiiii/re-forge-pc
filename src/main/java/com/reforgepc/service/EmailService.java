package com.reforgepc.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(
            String email,
            String otp,
            LocalDateTime expiresAt) {

        long minutesRemaining = (long) Math.ceil(
                Duration.between(LocalDateTime.now(), expiresAt).toSeconds() / 60.0
        );

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("ReForgePC - Mã OTP");
        message.setText(
                "Mã OTP của bạn là: " + otp + "\n" +
                "Mã này có hiệu lực trong " + minutesRemaining + " phút.\n" +
                "Nếu bạn không phải là người yêu cầu, vui lòng bỏ qua email này."
        );

        mailSender.send(message);
    }
}