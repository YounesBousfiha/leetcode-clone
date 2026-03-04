package com.leetcode.aiassistant.application.service.impl;

import com.leetcode.aiassistant.application.service.IAIAssistService;
import com.leetcode.aiassistant.presentation.dto.AIHintResponse;
import com.leetcode.aiassistant.presentation.dto.CodeReviewResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIAssistService implements IAIAssistService {

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    // TODO: Implement OpenAI API integration
    // For now, return placeholder responses

    @Override
    public AIHintResponse getHint(String problemSlug, String userCode, String language) {
        log.info("Getting hint for problem: {} in language: {}", problemSlug, language);

        // TODO: Call OpenAI API with problem context and user code
        // Analyze the code and provide contextual hints

        return AIHintResponse.builder()
                .hint("Consider using a hash map to store seen elements for O(n) time complexity.")
                .complexity("Time: O(n), Space: O(n)")
                .approach("Hash Table / Two Pointer")
                .build();
    }

    @Override
    public AIHintResponse explainProblem(String problemSlug) {
        log.info("Explaining problem: {}", problemSlug);

        // TODO: Fetch problem details and generate explanation using OpenAI

        return AIHintResponse.builder()
                .hint("This problem requires understanding of array traversal and hash maps.")
                .complexity("Optimal solution: O(n) time, O(n) space")
                .approach("Use a hash map to track complementary values as you iterate")
                .build();
    }

    @Override
    public CodeReviewResponse reviewCode(String code, String language, String problemSlug) {
        log.info("Reviewing code for problem: {} in language: {}", problemSlug, language);

        // TODO: Implement OpenAI code review
        // Analyze code quality, bugs, performance issues

        return CodeReviewResponse.builder()
                .overallFeedback("Good solution! Consider edge cases and optimization.")
                .improvements(Arrays.asList(
                        "Add input validation for null/empty arrays",
                        "Consider using more descriptive variable names",
                        "Add comments for complex logic"
                ))
                .bugs(Arrays.asList(
                        "Potential IndexOutOfBoundsException on line 5"
                ))
                .codeQualityScore(75)
                .build();
    }

    // TODO: Implement rate limiting with Redis
    // TODO: Add OpenAI API client
    // TODO: Add prompt templates
}

