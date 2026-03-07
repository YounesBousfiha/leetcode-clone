package com.leetcode.authservice.presentation.controller;


import com.leetcode.authservice.application.service.IAuthService;
import com.leetcode.authservice.presentation.dto.request.*;
import com.leetcode.authservice.presentation.dto.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "User authentication and account management endpoints")
public class AuthController {

    private final IAuthService authService;


    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with email and password, returns JWT tokens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "403", description = "Account not verified")
    })
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(this.authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create a new user account and send verification email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Registration successful"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        String message = this.authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", message
                ));
    }

    @GetMapping("/verify")
    @Operation(summary = "Verify email", description = "Verify user email address using the token sent by email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account verified successfully"),
        @ApiResponse(responseCode = "404", description = "Token not found"),
        @ApiResponse(responseCode = "410", description = "Token expired or already used")
    })
    public ResponseEntity<String> verify(
            @Parameter(description = "Email verification token") @RequestParam(name = "token") String verificationToken
    ) {
        return ResponseEntity.ok(this.authService.verifyAccount(verificationToken));
    }

    @PostMapping("/forget-password")
    @Operation(summary = "Forgot password", description = "Initiate password reset process - sends reset link to email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reset email sent"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<String> forgotPassword(@RequestBody ForgetPasswordRequest request) {
        return ResponseEntity.ok(this.authService.initiatePasswordReset(request));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password using the token received by email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successful"),
        @ApiResponse(responseCode = "410", description = "Token expired or already used")
    })
    public ResponseEntity<String> resetPassword(
            @Parameter(description = "Password reset token") @RequestParam("token") String token,
            @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(this.authService.resetPassword(request, token));
    }


    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token", description = "Generate a new access token using a valid refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "New tokens generated"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.refreshToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidate access and refresh tokens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Logout successful")
    })
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
