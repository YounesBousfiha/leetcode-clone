package com.leetcode.authservice.infrastructure.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtUtil Unit Tests")
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String TEST_SECRET = "test-secret-key-for-jwt-signing-minimum-256-bits-long";
    private final long TEST_EXPIRATION = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expirationTime", TEST_EXPIRATION);
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void shouldGenerateValidToken() {
        // Given
        String username = "test@example.com";
        String userId = "123e4567-e89b-12d3-a456-426614174000";
        String role = "USER";

        // When
        String token = jwtUtil.generateToken(username, userId, role);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
    }

    @Test
    @DisplayName("Should validate and decode valid token")
    void shouldValidateAndDecodeValidToken() {
        // Given
        String username = "test@example.com";
        String userId = "123e4567-e89b-12d3-a456-426614174000";
        String role = "USER";
        String token = jwtUtil.generateToken(username, userId, role);

        // When
        DecodedJWT decodedJWT = jwtUtil.validateToken(token);

        // Then
        assertThat(decodedJWT).isNotNull();
        assertThat(decodedJWT.getSubject()).isEqualTo(username);
        assertThat(decodedJWT.getClaim("userId").asString()).isEqualTo(userId);
        assertThat(decodedJWT.getClaim("role").asString()).isEqualTo(role);
    }

    @Test
    @DisplayName("Should extract username from token")
    void shouldExtractUsernameFromToken() {
        // Given
        String username = "test@example.com";
        String userId = "123";
        String role = "USER";
        String token = jwtUtil.generateToken(username, userId, role);

        // When
        String extractedUsername = jwtUtil.extractUserName(token);

        // Then
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    @DisplayName("Should extract userId from token")
    void shouldExtractUserIdFromToken() {
        // Given
        String username = "test@example.com";
        String userId = "123e4567-e89b-12d3-a456-426614174000";
        String role = "USER";
        String token = jwtUtil.generateToken(username, userId, role);

        // When
        String extractedUserId = jwtUtil.extractUserId(token);

        // Then
        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should extract role from token")
    void shouldExtractRoleFromToken() {
        // Given
        String username = "test@example.com";
        String userId = "123";
        String role = "ADMIN";
        String token = jwtUtil.generateToken(username, userId, role);

        // When
        String extractedRole = jwtUtil.extractRole(token);

        // Then
        assertThat(extractedRole).isEqualTo(role);
    }

    @Test
    @DisplayName("Should extract expiration date from token")
    void shouldExtractExpirationDateFromToken() {
        // Given
        String username = "test@example.com";
        String userId = "123";
        String role = "USER";
        String token = jwtUtil.generateToken(username, userId, role);

        // When
        Date expirationDate = jwtUtil.expirationDate(token);

        // Then
        assertThat(expirationDate).isNotNull();
        assertThat(expirationDate).isAfter(new Date());
    }

    @Test
    @DisplayName("Should throw exception for invalid token")
    void shouldThrowExceptionForInvalidToken() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When & Then
        assertThatThrownBy(() -> jwtUtil.validateToken(invalidToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Token Invalid");
    }

    @Test
    @DisplayName("Should throw exception for token with wrong signature")
    void shouldThrowExceptionForTokenWithWrongSignature() {
        // Given
        String username = "test@example.com";
        String userId = "123";
        String role = "USER";
        String token = jwtUtil.generateToken(username, userId, role);

        // Change secret to simulate wrong signature
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", "different-secret-key");

        // When & Then
        assertThatThrownBy(() -> jwtUtil.validateToken(token))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Token Invalid");
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void shouldGenerateDifferentTokensForDifferentUsers() {
        // Given
        String token1 = jwtUtil.generateToken("user1@example.com", "1", "USER");
        String token2 = jwtUtil.generateToken("user2@example.com", "2", "USER");

        // Then
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Should have valid expiration time")
    void shouldHaveValidExpirationTime() {
        // Given
        String username = "test@example.com";
        String userId = "123";
        String role = "USER";
        long beforeGeneration = System.currentTimeMillis();

        // When
        String token = jwtUtil.generateToken(username, userId, role);
        Date expirationDate = jwtUtil.expirationDate(token);

        // Then
        long expectedExpiration = beforeGeneration + TEST_EXPIRATION;
        long actualExpiration = expirationDate.getTime();

        // Allow 1 second tolerance
        assertThat(actualExpiration).isBetween(expectedExpiration - 1000, expectedExpiration + 1000);
    }
}

