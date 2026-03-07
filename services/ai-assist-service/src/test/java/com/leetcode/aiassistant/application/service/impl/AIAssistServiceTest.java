package com.leetcode.aiassistant.application.service.impl;

import com.leetcode.aiassistant.presentation.dto.AIHintResponse;
import com.leetcode.aiassistant.presentation.dto.CodeReviewResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIAssistServiceTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    @InjectMocks
    private AIAssistService aiAssistService;

    private void setupChatClientMock(String response) {
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(response);
    }

    @Test
    @DisplayName("getHint - should return parsed hint response")
    void getHint_shouldReturnParsedResponse() {
        String llmResponse = "HINT: Use a hash map for O(1) lookup\nCOMPLEXITY: O(n) time, O(n) space\nAPPROACH: Two-pass hash table";
        setupChatClientMock(llmResponse);

        AIHintResponse result = aiAssistService.getHint("two-sum", "def twoSum(nums, target): pass", "python");

        assertThat(result.hint()).contains("hash map");
        assertThat(result.complexity()).contains("O(n)");
        assertThat(result.approach()).contains("hash table");
    }

    @Test
    @DisplayName("getHint - should return fallback on exception")
    void getHint_shouldReturnFallbackOnError() {
        when(chatClient.prompt()).thenThrow(new RuntimeException("API error"));

        AIHintResponse result = aiAssistService.getHint("two-sum", "code", "java");

        assertThat(result.hint()).contains("Unable to generate hint");
        assertThat(result.approach()).isNotNull();
    }

    @Test
    @DisplayName("explainProblem - should return parsed explanation")
    void explainProblem_shouldReturnParsedExplanation() {
        String llmResponse = "HINT: Find two numbers that add up to target\nCOMPLEXITY: O(n)\nAPPROACH: Hash Map";
        setupChatClientMock(llmResponse);

        AIHintResponse result = aiAssistService.explainProblem("two-sum");

        assertThat(result.hint()).contains("two numbers");
        assertThat(result.complexity()).isNotNull();
    }

    @Test
    @DisplayName("explainProblem - should return fallback on exception")
    void explainProblem_shouldReturnFallbackOnError() {
        when(chatClient.prompt()).thenThrow(new RuntimeException("API error"));

        AIHintResponse result = aiAssistService.explainProblem("two-sum");

        assertThat(result.hint()).contains("coding challenge");
        assertThat(result.complexity()).isEqualTo("Varies by approach");
    }

    @Test
    @DisplayName("reviewCode - should return parsed code review")
    void reviewCode_shouldReturnParsedReview() {
        String llmResponse = """
                FEEDBACK: Good solution with clean code structure
                IMPROVEMENTS:
                - Add input validation
                - Consider edge cases
                BUGS:
                - Potential null pointer on line 5
                SCORE: 85
                """;
        setupChatClientMock(llmResponse);

        CodeReviewResponse result = aiAssistService.reviewCode("class Solution {}", "java", "two-sum");

        assertThat(result.overallFeedback()).contains("Good solution");
        assertThat(result.improvements()).isNotEmpty();
        assertThat(result.bugs()).isNotEmpty();
        assertThat(result.codeQualityScore()).isEqualTo(85);
    }

    @Test
    @DisplayName("reviewCode - should return fallback on exception")
    void reviewCode_shouldReturnFallbackOnError() {
        when(chatClient.prompt()).thenThrow(new RuntimeException("API error"));

        CodeReviewResponse result = aiAssistService.reviewCode("code", "java", "two-sum");

        assertThat(result.overallFeedback()).contains("Unable to review");
        assertThat(result.codeQualityScore()).isEqualTo(70);
    }

    @Test
    @DisplayName("getHint - should handle unparseable LLM response gracefully")
    void getHint_shouldHandleUnparseableResponse() {
        String rawResponse = "Here is a free-form hint without the expected format.";
        setupChatClientMock(rawResponse);

        AIHintResponse result = aiAssistService.getHint("two-sum", "code", "python");

        assertThat(result.hint()).isNotNull();
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("reviewCode - should handle missing score in response")
    void reviewCode_shouldHandleMissingScore() {
        String llmResponse = """
                FEEDBACK: Decent code
                IMPROVEMENTS:
                - Improve naming
                BUGS:
                - None found
                """;
        setupChatClientMock(llmResponse);

        CodeReviewResponse result = aiAssistService.reviewCode("code", "python", "test");

        assertThat(result.codeQualityScore()).isEqualTo(70); // default
        assertThat(result.overallFeedback()).contains("Decent");
    }
}

