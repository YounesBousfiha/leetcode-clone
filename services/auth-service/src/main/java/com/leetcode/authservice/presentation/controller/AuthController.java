package com.leetcode.authservice.presentation.controller;


import com.leetcode.authservice.application.service.IAuthService;
import com.leetcode.authservice.presentation.dto.request.*;
import com.leetcode.authservice.presentation.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;


    @GetMapping("/demo")
    public ResponseEntity<String> demo() {
        return ResponseEntity.ok("Hello from Secured Endpoint");
    }

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
            @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(this.authService.resetPassword(request, token));
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody LogoutRequest request
    ) {
        String accessToken = authHeader;
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }

        authService.logout(accessToken, request.refreshToken());

        return ResponseEntity.noContent().build();
    }
}
