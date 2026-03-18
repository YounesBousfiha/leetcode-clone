package com.leetcode.aiassistant.infrastructure.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RateLimitingInterceptor Unit Tests")
class RateLimitingInterceptorTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private RateLimitingInterceptor interceptor;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        interceptor = new RateLimitingInterceptor(redisTemplate);
        ReflectionTestUtils.setField(interceptor, "requestsPerMinute", 10);
        ReflectionTestUtils.setField(interceptor, "requestsPerHour", 100);

        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        responseWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(responseWriter);
        lenient().when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    @DisplayName("Should allow request within rate limit")
    void shouldAllowRequestWithinRateLimit() throws Exception {
        // Given
        String userId = "user123";
        when(request.getHeader("X-User-Id")).thenReturn(userId);
        when(valueOperations.increment(anyString())).thenReturn(1L);

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result).isTrue();
        verify(valueOperations, times(2)).increment(anyString()); // minute and hour
        verify(redisTemplate, times(2)).expire(anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    @DisplayName("Should block request exceeding minute limit")
    void shouldBlockRequestExceedingMinuteLimit() throws Exception {
        // Given
        String userId = "user123";
        when(request.getHeader("X-User-Id")).thenReturn(userId);
        when(valueOperations.increment(contains("minute")))
                .thenReturn(11L); // Exceeds 10 per minute

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result).isFalse();
        verify(response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        verify(response).setContentType("application/json");

        String responseContent = responseWriter.toString();
        assertThat(responseContent).contains("Rate limit exceeded");
        assertThat(responseContent).contains("10 requests per minute");
    }

    @Test
    @DisplayName("Should block request exceeding hour limit")
    void shouldBlockRequestExceedingHourLimit() throws Exception {
        // Given
        String userId = "user123";
        when(request.getHeader("X-User-Id")).thenReturn(userId);
        when(valueOperations.increment(contains("minute"))).thenReturn(5L);
        when(valueOperations.increment(contains("hour"))).thenReturn(101L); // Exceeds 100 per hour

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result).isFalse();
        verify(response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());

        String responseContent = responseWriter.toString();
        assertThat(responseContent).contains("Rate limit exceeded");
        assertThat(responseContent).contains("100 requests per hour");
    }

    @Test
    @DisplayName("Should set expiration on first request")
    void shouldSetExpirationOnFirstRequest() throws Exception {
        // Given
        String userId = "user123";
        when(request.getHeader("X-User-Id")).thenReturn(userId);
        when(valueOperations.increment(anyString())).thenReturn(1L);

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result).isTrue();
        verify(redisTemplate).expire(contains("minute"), eq(1L), eq(TimeUnit.MINUTES));
        verify(redisTemplate).expire(contains("hour"), eq(1L), eq(TimeUnit.HOURS));
    }

    @Test
    @DisplayName("Should not set expiration on subsequent requests")
    void shouldNotSetExpirationOnSubsequentRequests() throws Exception {
        // Given
        String userId = "user123";
        when(request.getHeader("X-User-Id")).thenReturn(userId);
        when(valueOperations.increment(anyString())).thenReturn(5L); // Not first request

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result).isTrue();
        verify(redisTemplate, never()).expire(anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    @DisplayName("Should skip rate limiting when user ID is null")
    void shouldSkipRateLimitingWhenUserIdIsNull() throws Exception {
        // Given
        when(request.getHeader("X-User-Id")).thenReturn(null);

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result).isTrue();
        verify(valueOperations, never()).increment(anyString());
    }

    @Test
    @DisplayName("Should allow request on Redis failure (fail open)")
    void shouldAllowRequestOnRedisFailure() throws Exception {
        // Given
        String userId = "user123";
        when(request.getHeader("X-User-Id")).thenReturn(userId);
        when(valueOperations.increment(anyString())).thenReturn(null); // Redis failure

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should use correct Redis key format")
    void shouldUseCorrectRedisKeyFormat() throws Exception {
        // Given
        String userId = "user123";
        when(request.getHeader("X-User-Id")).thenReturn(userId);
        when(valueOperations.increment(anyString())).thenReturn(1L);

        // When
        interceptor.preHandle(request, response, new Object());

        // Then
        verify(valueOperations).increment("rate_limit:ai:minute:user123");
        verify(valueOperations).increment("rate_limit:ai:hour:user123");
    }

    @Test
    @DisplayName("Should handle multiple sequential requests correctly")
    void shouldHandleMultipleSequentialRequestsCorrectly() throws Exception {
        // Given
        String userId = "user123";
        when(request.getHeader("X-User-Id")).thenReturn(userId);

        // Simulate 10 successful requests
        for (int i = 1; i <= 10; i++) {
            when(valueOperations.increment(contains("minute"))).thenReturn((long) i);
            when(valueOperations.increment(contains("hour"))).thenReturn((long) i);
            boolean result = interceptor.preHandle(request, response, new Object());
            assertThat(result).isTrue();
        }

        // 11th request should be blocked
        when(valueOperations.increment(contains("minute"))).thenReturn(11L);
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should handle different users independently")
    void shouldHandleDifferentUsersIndependently() throws Exception {
        // Given
        when(valueOperations.increment(anyString())).thenReturn(1L);

        // User 1
        when(request.getHeader("X-User-Id")).thenReturn("user1");
        boolean result1 = interceptor.preHandle(request, response, new Object());

        // User 2
        when(request.getHeader("X-User-Id")).thenReturn("user2");
        boolean result2 = interceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result1).isTrue();
        assertThat(result2).isTrue();
        verify(valueOperations).increment("rate_limit:ai:minute:user1");
        verify(valueOperations).increment("rate_limit:ai:minute:user2");
    }
}

