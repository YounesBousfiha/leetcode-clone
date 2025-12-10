package com.leetcode.authservice.infrastructure.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN:";

    public void saveRefreshToken(String email, String refreshToken) {
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN + refreshToken,
                email,
                Duration.ofDays(7)
        );
    }

    public String extractRefreshTokenSubject(String refreshToken) {
        return redisTemplate.opsForValue().get(REFRESH_TOKEN + refreshToken);
    }

    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete(REFRESH_TOKEN + refreshToken);
    }

    public void blackListAccessToken(String accessToken, long ttlInMillis) {
        if(ttlInMillis > 0) {
            redisTemplate.opsForValue().set(
                    "BLACKLIST:" + accessToken,
                    "true",
                    Duration.ofMillis(ttlInMillis)
            );
        }
    }

    public boolean isTokenBlackListed(String accessToken) {
        return  Boolean.TRUE.equals(redisTemplate.hasKey("BLACKLIST:" + accessToken));
    }
}
