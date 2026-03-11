package com.leetcode.aiassistant.infrastructure.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Spring AI with DeepSeek
 * Using official Spring AI DeepSeek starter
 * 
 * Auto-configuration is provided by spring-ai-starter-model-deepseek
 * Properties configured via application.properties:
 * - spring.ai.deepseek.api-key
 * - spring.ai.deepseek.base-url
 * - spring.ai.deepseek.chat.options.model
 * - spring.ai.deepseek.chat.options.temperature
 * - spring.ai.deepseek.chat.options.max-tokens
 */
@Configuration
public class DeepSeekConfig {

    /**
     * ChatClient builder with default system message for LeetCode assistance
     * 
     * @param chatModel Auto-configured DeepSeekChatModel bean
     * @return Configured ChatClient
     */
    @Bean
    public ChatClient chatClient(DeepSeekChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                        You are an expert coding assistant specializing in algorithm problems and code optimization.
                        You help developers understand algorithms, data structures, and best practices.
                        Be concise, clear, and educational in your responses.
                        When giving hints, guide the user without revealing the complete solution.
                        Always structure your responses according to the requested format.
                        """)
                .build();
    }
}

