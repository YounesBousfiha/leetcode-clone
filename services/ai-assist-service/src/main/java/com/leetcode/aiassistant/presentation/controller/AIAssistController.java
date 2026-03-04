package com.leetcode.aiassistant.presentation.controller;

import com.leetcode.aiassistant.application.service.IAIAssistService;
import com.leetcode.aiassistant.presentation.dto.AIHintRequest;
import com.leetcode.aiassistant.presentation.dto.AIHintResponse;
import com.leetcode.aiassistant.presentation.dto.CodeReviewRequest;
import com.leetcode.aiassistant.presentation.dto.CodeReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AI Assist Controller - Simplified with Spring AI + DeepSeek only
 */
@RestController
@RequestMapping("/api/ai-assist")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "AI Assist", description = "AI-powered code assistance using LLM (DeepSeek)")
public class AIAssistController {

    private final IAIAssistService aiAssistService;

    @PostMapping("/hint")
    @Operation(summary = "Get code hint", description = "Analyzes user code and provides contextual hints to help solve the problem")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hint generated successfully"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AIHintResponse> getHint(@RequestBody AIHintRequest request) {
        return ResponseEntity.ok(aiAssistService.getHint(
                request.problemSlug(),
                request.userCode(),
                request.language()
        ));
    }

    @GetMapping("/explain/{problemSlug}")
    @Operation(summary = "Explain problem", description = "Provides a detailed explanation of the problem and suggested approaches")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Explanation generated successfully"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded"),
        @ApiResponse(responseCode = "404", description = "Problem not found")
    })
    public ResponseEntity<AIHintResponse> explainProblem(@PathVariable String problemSlug) {
        return ResponseEntity.ok(aiAssistService.explainProblem(problemSlug));
    }

    @PostMapping("/review")
    @Operation(summary = "Review code", description = "Provides comprehensive code review including feedback, improvements, and bug detection")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Code review completed successfully"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded"),
        @ApiResponse(responseCode = "400", description = "Invalid code or language")
    })
    public ResponseEntity<CodeReviewResponse> reviewCode(@RequestBody CodeReviewRequest request) {
        return ResponseEntity.ok(aiAssistService.reviewCode(
                request.code(),
                request.language(),
                request.problemSlug()
        ));
    }
}

