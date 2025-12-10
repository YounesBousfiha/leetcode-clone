package com.leetcode.authservice.application.service;


import com.leetcode.authservice.application.producer.EventPublisher;
import com.leetcode.authservice.domain.entity.EmailVerification;
import com.leetcode.authservice.domain.entity.PasswordResetToken;
import com.leetcode.authservice.domain.entity.User;
import com.leetcode.authservice.domain.enums.Role;
import com.leetcode.authservice.domain.event.PasswordResetEvent;
import com.leetcode.authservice.domain.event.UserRegisteredEvent;
import com.leetcode.authservice.domain.exception.*;
import com.leetcode.authservice.infrastructure.repository.EmailVerificationRepository;
import com.leetcode.authservice.infrastructure.repository.PasswordResetTokenRepository;
import com.leetcode.authservice.infrastructure.repository.UserRepository;
import com.leetcode.authservice.infrastructure.security.JwtUtil;
import com.leetcode.authservice.presentation.dto.request.ForgetPasswordRequest;
import com.leetcode.authservice.presentation.dto.request.LoginRequest;
import com.leetcode.authservice.presentation.dto.request.RegisterRequest;
import com.leetcode.authservice.presentation.dto.request.ResetPasswordRequest;
import com.leetcode.authservice.presentation.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final JwtUtil jwtUtil;
    private final EventPublisher eventPublisher;

    public String register(RegisterRequest request) {

        if(userRepository.existsByEmail(request.email())) {
            throw new SameEmailRegistration("Email Already Exists");
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        log.debug("Password encoded for user: {}", request.email());

        User user = User.builder()
                .displayName(request.displayName())
                .email(request.email())
                .password(encodedPassword)
                .role(Role.USER)
                .verified(false)
                .build();

        this.userRepository.save(user);
        log.info("User saved with ID: {}, Email: {}", user.getId(), user.getEmail());

        String token = UUID.randomUUID().toString();

        EmailVerification emailVerification = EmailVerification.builder()
                .uuid(UUID.randomUUID())
                .verificationToken(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .isUsed(false)
                .build();

        this.emailVerificationRepository.save(emailVerification);

        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .userId(user.getId().toString())
                .verificationToken(token)
                .build();

        eventPublisher.sendUserRegisteredEvent(event);

        return "Registration successful. Please check your email to verify your account.";
    }

    public LoginResponse login(LoginRequest request) {

        User user = this.userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentials("Email/Password Incorrect"));


        boolean passwordMatches = passwordEncoder.matches(request.password(), user.getPassword());

        if(!passwordMatches) {
            throw new InvalidCredentials("Email/Password Incorrect");
        }

        if(!user.isVerified()) {
            throw new UnverifiedUser("Your Account Email is not verified. please verify your Email");
        }

        String jwt = jwtUtil.generateToken(user.getDisplayName(), user.getId().toString(), user.getRole().toString());

        return LoginResponse.builder()
                .accessToken(jwt)
                .email(user.getEmail())
                .userId(user.getId().toString())
                .role(user.getRole().toString())
                .build();
    }

    public String verifyAccount(String token) {
        EmailVerification verificationToken = this.emailVerificationRepository.findByVerificationToken(token)
                .orElseThrow(() -> new ResourceNotFound("Resource Not Found"));

        if(verificationToken.isUsed() || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenExpire("Token is Expired or Already used");
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        this.userRepository.save(user);

        verificationToken.setUsed(true);
        this.emailVerificationRepository.save(verificationToken);


        return "Account verified successfully. now You can Login";
    }

    public String initiatePasswordReset(ForgetPasswordRequest request) {
        User user = this.userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .used(false)
                .expiryDate(LocalDateTime.now().plusMinutes(30))
                .build();

        this.resetTokenRepository.save(resetToken);
        PasswordResetEvent passwordResetEvent = PasswordResetEvent.builder()
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .token(token)
                .build();

        eventPublisher.sendPasswordResetEvent(passwordResetEvent);

        return "Password reset link sent to your email";
    }


    public String resetPassword(ResetPasswordRequest request, String token) {

        PasswordResetToken resetToken = this.resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("No such Token"));

        log.info("Token: {}", token);
        log.info("new Password: {}", request.newPassword());
        if(resetToken.isUsed() || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenExpire("Token Expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        this.userRepository.save(user);

        resetToken.setUsed(true);
        this.resetTokenRepository.save(resetToken);

        return "Password reset successfully. you can now Login";
    }
}
