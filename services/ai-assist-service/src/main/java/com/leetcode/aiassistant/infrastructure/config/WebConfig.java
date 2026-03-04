package com.leetcode.aiassistant.infrastructure.config;

import com.leetcode.aiassistant.infrastructure.ratelimit.RateLimitingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RateLimitingInterceptor rateLimitingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitingInterceptor)
                .addPathPatterns("/api/ai-assist/**")
                .excludePathPatterns("/api/ai-assist/providers"); // Exclude providers endpoint from rate limiting
    }
}

