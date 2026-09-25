package com.reforgepc.service;

import com.reforgepc.entity.Otp;
import com.reforgepc.entity.OtpPurpose;
import com.reforgepc.repository.OtpRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRATION_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 5;
    private static final int OTP_REQUEST_COOLDOWN_SECONDS = 60;

    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public OtpService(
            OtpRepository otpRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean canRequestOtp(String email, OtpPurpose purpose) {
        return otpRepository
                .findFirstByEmailAndPurposeOrderByIdDesc(email, purpose)
                .map(otp -> {
                    LocalDateTime cooldownEndsAt =
                            otp.getCreatedAt().plusSeconds(OTP_REQUEST_COOLDOWN_SECONDS);

                    return !LocalDateTime.now().isBefore(cooldownEndsAt);
                })
                .orElse(true);
    }

    public Otp generateOtp(String email, OtpPurpose purpose) {

        if (!canRequestOtp(email, purpose)) {
            throw new IllegalStateException(
                    "Vui lòng chờ trước khi yêu cầu mã OTP mới."
            );
        }

        invalidatePreviousOtps(email, purpose);

        String otp = generateCode();

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = createdAt.plusMinutes(OTP_EXPIRATION_MINUTES);

        Otp otpEntity = new Otp();
        otpEntity.setEmail(email);
        otpEntity.setPurpose(purpose);
        otpEntity.setOtpHash(passwordEncoder.encode(otp));
        otpEntity.setRawOtp(otp);
        otpEntity.setCreatedAt(createdAt);
        otpEntity.setExpiresAt(expiresAt);
        otpEntity.setAttempts(0);
        otpEntity.setUsed(false);

        otpRepository.save(otpEntity);

        return otpEntity;
    }

    public boolean verifyOtp(String email, OtpPurpose purpose, String otp) {

        Otp otpEntity = otpRepository
                .findFirstByEmailAndPurposeAndUsedFalseOrderByIdDesc(email, purpose)
                .orElse(null);

        if (otpEntity == null) {
            return false;
        }

        if (otpEntity.isUsed()) {
            return false;
        }

        if (LocalDateTime.now().isAfter(otpEntity.getExpiresAt())) {
            return false;
        }

        if (otpEntity.getAttempts() >= MAX_ATTEMPTS) {
            return false;
        }

        otpEntity.setAttempts(otpEntity.getAttempts() + 1);

        if (!passwordEncoder.matches(otp, otpEntity.getOtpHash())) {
            otpRepository.save(otpEntity);
            return false;
        }

        otpEntity.setUsed(true);
        otpRepository.save(otpEntity);

        return true;
    }

    private void invalidatePreviousOtps(String email, OtpPurpose purpose) {

        otpRepository
                .findByEmailAndPurposeAndUsedFalse(email, purpose)
                .forEach(otp -> otp.setUsed(true));

        otpRepository.flush();
    }

    private String generateCode() {

        int number = secureRandom.nextInt(1_000_000);

        return String.format("%0" + OTP_LENGTH + "d", number);
    }
}