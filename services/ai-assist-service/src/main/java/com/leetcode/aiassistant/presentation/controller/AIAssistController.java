package com.leetcode.aiassistant.presentation.controller;

import com.leetcode.aiassistant.application.service.IAIAssistService;
import com.leetcode.aiassistant.presentation.dto.AIHintRequest;
import com.leetcode.aiassistant.presentation.dto.AIHintResponse;
import com.leetcode.aiassistant.presentation.dto.CodeReviewRequest;
import com.leetcode.aiassistant.presentation.dto.CodeReviewResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai-assist")
@RequiredArgsConstructor
@Slf4j
public class AIAssistController {

    private final IAIAssistService aiAssistService;

    @PostMapping("/hint")
    public ResponseEntity<AIHintResponse> getHint(@RequestBody AIHintRequest request) {
        return ResponseEntity.ok(aiAssistService.getHint(
                request.problemSlug(),
                request.userCode(),
                request.language()
        ));
    }

    @GetMapping("/explain/{problemSlug}")
    public ResponseEntity<AIHintResponse> explainProblem(@PathVariable String problemSlug) {
        return ResponseEntity.ok(aiAssistService.explainProblem(problemSlug));
    }

    @PostMapping("/review")
    public ResponseEntity<CodeReviewResponse> reviewCode(@RequestBody CodeReviewRequest request) {
        return ResponseEntity.ok(aiAssistService.reviewCode(
                request.code(),
                request.language(),
                request.problemSlug()
        ));
    }
}

