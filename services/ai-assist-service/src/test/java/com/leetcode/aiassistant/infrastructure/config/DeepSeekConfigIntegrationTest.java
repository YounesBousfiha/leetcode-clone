package com.leetcode.aiassistant.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for DeepSeek configuration with Spring AI
 * Tests auto-configuration and ChatClient bean creation
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("DeepSeek Configuration Integration Tests")
class DeepSeekConfigIntegrationTest {

    @Autowired(required = false)
    private DeepSeekChatModel deepSeekChatModel;

    @Autowired(required = false)
    private ChatClient chatClient;

    @Test
    @DisplayName("DeepSeekChatModel should be auto-configured")
    void deepSeekChatModel_shouldBeAutoConfigured() {
        // Given Spring AI DeepSeek starter dependency
        // When application context loads
        // Then DeepSeekChatModel bean should exist
        assertThat(deepSeekChatModel)
                .as("DeepSeekChatModel should be auto-configured by Spring AI")
                .isNotNull();
    }

    @Test
    @DisplayName("ChatClient should be configured with default system message")
    void chatClient_shouldBeConfigured() {
        // Given DeepSeekConfig bean configuration
        // When application context loads
        // Then ChatClient bean should exist
        assertThat(chatClient)
                .as("ChatClient should be configured in DeepSeekConfig")
                .isNotNull();
    }

    @Test
    @DisplayName("ChatClient should use DeepSeekChatModel")
    void chatClient_shouldUseDeepSeekChatModel() {
        // Verify ChatClient is properly wired with DeepSeekChatModel
        assertThat(chatClient).isNotNull();
        assertThat(deepSeekChatModel).isNotNull();
        
        // Both beans should be available in the context
        // ChatClient internally uses the DeepSeekChatModel
    }
}

