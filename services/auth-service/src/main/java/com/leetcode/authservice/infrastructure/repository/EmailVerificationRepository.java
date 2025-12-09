package com.leetcode.authservice.infrastructure.repository;


import com.leetcode.authservice.domain.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, UUID> {
    Optional<EmailVerification> findByVerificationToken(String token);
}
