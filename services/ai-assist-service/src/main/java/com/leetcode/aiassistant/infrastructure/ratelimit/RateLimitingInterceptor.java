package com.leetcode.aiassistant.infrastructure.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${ai.rate-limit.requests-per-minute:10}")
    private int requestsPerMinute;

    @Value("${ai.rate-limit.requests-per-hour:100}")
    private int requestsPerHour;

    private static final String RATE_LIMIT_MINUTE_PREFIX = "rate_limit:ai:minute:";
    private static final String RATE_LIMIT_HOUR_PREFIX = "rate_limit:ai:hour:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = extractUserId(request);

        if (userId == null) {
            log.warn("Rate limiting skipped: No user ID found in request");
            return true; // Skip rate limiting for unauthenticated requests (handled by gateway)
        }

        // Check minute limit
        Long minuteRequests = getCurrentCount(userId, RATE_LIMIT_MINUTE_PREFIX);
        if (!checkRateLimit(userId, RATE_LIMIT_MINUTE_PREFIX, requestsPerMinute, 1, TimeUnit.MINUTES)) {
            log.warn("Rate limit exceeded for user {} (per minute)", userId);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.setHeader("X-RateLimit-Limit", String.valueOf(requestsPerMinute));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("X-RateLimit-Reset", String.valueOf(getResetTime(userId, RATE_LIMIT_MINUTE_PREFIX)));
            response.setHeader("Retry-After", "60");
            response.getWriter().write(String.format(
                "{\"error\":\"Rate limit exceeded\",\"message\":\"Maximum %d requests per minute allowed\",\"retryAfter\":60}",
                requestsPerMinute
            ));
            return false;
        }

        // Check hour limit
        if (!checkRateLimit(userId, RATE_LIMIT_HOUR_PREFIX, requestsPerHour, 1, TimeUnit.HOURS)) {
            log.warn("Rate limit exceeded for user {} (per hour)", userId);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.setHeader("X-RateLimit-Limit", String.valueOf(requestsPerHour));
            response.setHeader("X-RateLimit-Remaining", "0");
            response.setHeader("X-RateLimit-Reset", String.valueOf(getResetTime(userId, RATE_LIMIT_HOUR_PREFIX)));
            response.setHeader("Retry-After", "3600");
            response.getWriter().write(String.format(
                "{\"error\":\"Rate limit exceeded\",\"message\":\"Maximum %d requests per hour allowed\",\"retryAfter\":3600}",
                requestsPerHour
            ));
            return false;
        }

        // Add rate limit info headers on successful requests
        long minuteRemaining = Math.max(0, requestsPerMinute - (minuteRequests != null ? minuteRequests + 1 : 1));
        response.setHeader("X-RateLimit-Limit", String.valueOf(requestsPerMinute));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(minuteRemaining));

        return true;
    }

    private boolean checkRateLimit(String userId, String prefix, int maxRequests, long duration, TimeUnit unit) {
        String key = prefix + userId;

        Long requests = redisTemplate.opsForValue().increment(key);

        if (requests == null) {
            log.error("Failed to increment rate limit counter for user {}", userId);
            return true; // Allow request on Redis failure (fail open)
        }

        if (requests == 1) {
            // First request, set expiration
            redisTemplate.expire(key, duration, unit);
        }

        boolean allowed = requests <= maxRequests;

        if (allowed) {
            log.debug("Rate limit check passed for user {}: {}/{} requests", userId, requests, maxRequests);
        }

        return allowed;
    }

    private Long getCurrentCount(String userId, String prefix) {
        String key = prefix + userId;
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            if (value instanceof String) {
                return Long.parseLong((String) value);
            }
        } catch (Exception e) {
            log.debug("Could not get current count for key {}", key);
        }
        return 0L;
    }

    private long getResetTime(String userId, String prefix) {
        String key = prefix + userId;
        try {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (ttl != null && ttl > 0) {
                return System.currentTimeMillis() / 1000 + ttl;
            }
        } catch (Exception e) {
            log.debug("Could not get TTL for key {}", key);
        }
        return System.currentTimeMillis() / 1000 + 60;
    }

    private String extractUserId(HttpServletRequest request) {
        // Extract user ID from header (set by API Gateway after JWT validation)
        String userId = request.getHeader("X-User-Id");

        if (userId == null || userId.isEmpty()) {
            // Fallback to extracting from Authorization header if needed
            log.debug("X-User-Id header not found, rate limiting may not work properly");
        }

        return userId;
    }
}

