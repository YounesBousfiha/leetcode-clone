package com.leetcode.authservice.infrastructure.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisService redisService;

    @Test
    @DisplayName("saveRefreshToken - should store token with 7-day TTL")
    void saveRefreshToken_shouldStoreTokenWithTTL() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        redisService.saveRefreshToken("user@test.com", "refresh-token-123");

        verify(valueOperations).set(
                eq("REFRESH_TOKEN:refresh-token-123"),
                eq("user@test.com"),
                eq(Duration.ofDays(7))
        );
    }

    @Test
    @DisplayName("extractRefreshTokenSubject - should return email for valid token")
    void extractRefreshTokenSubject_shouldReturnEmail() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("REFRESH_TOKEN:refresh-token-123")).thenReturn("user@test.com");

        String email = redisService.extractRefreshTokenSubject("refresh-token-123");

        assertThat(email).isEqualTo("user@test.com");
    }

    @Test
    @DisplayName("extractRefreshTokenSubject - should return null for invalid token")
    void extractRefreshTokenSubject_shouldReturnNullForInvalidToken() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("REFRESH_TOKEN:invalid-token")).thenReturn(null);

        String email = redisService.extractRefreshTokenSubject("invalid-token");

        assertThat(email).isNull();
    }

    @Test
    @DisplayName("deleteRefreshToken - should delete the token key")
    void deleteRefreshToken_shouldDeleteKey() {
        redisService.deleteRefreshToken("refresh-token-123");

        verify(redisTemplate).delete("REFRESH_TOKEN:refresh-token-123");
    }

    @Test
    @DisplayName("blackListAccessToken - should blacklist token with TTL")
    void blackListAccessToken_shouldBlacklistWithTTL() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        redisService.blackListAccessToken("access-token-123", 60000L);

        verify(valueOperations).set(
                eq("BLACKLIST:access-token-123"),
                eq("true"),
                eq(Duration.ofMillis(60000))
        );
    }

    @Test
    @DisplayName("blackListAccessToken - should not store when TTL is zero or negative")
    void blackListAccessToken_shouldNotStoreWhenTTLInvalid() {
        redisService.blackListAccessToken("access-token-123", 0L);

        verifyNoInteractions(valueOperations);
    }

    @Test
    @DisplayName("isTokenBlackListed - should return true for blacklisted token")
    void isTokenBlackListed_shouldReturnTrue() {
        when(redisTemplate.hasKey("BLACKLIST:access-token-123")).thenReturn(true);

        boolean result = redisService.isTokenBlackListed("access-token-123");

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isTokenBlackListed - should return false for non-blacklisted token")
    void isTokenBlackListed_shouldReturnFalse() {
        when(redisTemplate.hasKey("BLACKLIST:access-token-123")).thenReturn(false);

        boolean result = redisService.isTokenBlackListed("access-token-123");

        assertThat(result).isFalse();
    }
}

