package com.leetcode.authservice.presentation.controller;


import com.leetcode.authservice.application.service.AuthService;
import com.leetcode.authservice.presentation.dto.request.ForgetPasswordRequest;
import com.leetcode.authservice.presentation.dto.request.LoginRequest;
import com.leetcode.authservice.presentation.dto.request.RegisterRequest;
import com.leetcode.authservice.presentation.dto.request.ResetPasswordRequest;
import com.leetcode.authservice.presentation.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(this.authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        String message = this.authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", message
                ));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(
            @RequestParam(name = "token") String verificationToken
    ) {
        return ResponseEntity.ok(this.authService.verifyAccount(verificationToken));
    }

    @PostMapping("/forget-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgetPasswordRequest request) {
        return ResponseEntity.ok(this.authService.initiatePasswordReset(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam("token") String token,
            @RequestBody String newPassword
    ) {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .token(token)
                .newPassword(newPassword)
                .build();
        return ResponseEntity.ok(this.authService.resetPassword(request));
    }
}
