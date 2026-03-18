package com.leetcode.aiassistant.infrastructure.config;

import com.leetcode.aiassistant.presentation.dto.AIHintResponse;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisSerializer<String> keySerializer = new StringRedisSerializer();

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)) // Cache LLM responses for 1 hour
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        RedisSerializationContext.SerializationPair<AIHintResponse> hintValueSerializer =
                RedisSerializationContext.SerializationPair.fromSerializer(
                        new Jackson2JsonRedisSerializer<>(AIHintResponse.class));

        RedisCacheConfiguration aiHintCacheConfig = defaultConfig.serializeValuesWith(hintValueSerializer);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(Map.of(
                        "ai_hints_v2", aiHintCacheConfig,
                        "ai_explanations_v2", aiHintCacheConfig
                ))
                .build();
    }
}

