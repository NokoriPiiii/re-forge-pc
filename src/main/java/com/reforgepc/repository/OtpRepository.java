package com.reforgepc.repository;

import com.reforgepc.entity.Otp;
import com.reforgepc.entity.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findFirstByEmailAndPurposeAndUsedFalseOrderByIdDesc(
            String email,
            OtpPurpose purpose
    );

    Optional<Otp> findFirstByEmailAndPurposeOrderByIdDesc(
            String email,
            OtpPurpose purpose
    );

    List<Otp> findByEmailAndPurposeAndUsedFalse(
            String email,
            OtpPurpose purpose
    );
}