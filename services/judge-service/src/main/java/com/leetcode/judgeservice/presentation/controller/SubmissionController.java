package com.leetcode.judgeservice.presentation.controller;


import com.leetcode.judgeservice.application.service.SubmissionService;
import com.leetcode.judgeservice.presentation.dto.SubmissionRequest;
import com.leetcode.judgeservice.presentation.dto.SubmissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<SubmissionResponse> submitCode(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody SubmissionRequest request
            ) {

        return ResponseEntity.ok(submissionService.submit(UUID.fromString(userId), request));
    }

    @GetMapping("/me")
    public ResponseEntity<Page<SubmissionResponse>> getMySubmissions(
            @RequestHeader("X-User-Id") String userId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(submissionService.getUserSubmissions(UUID.fromString(userId), pageable));
    }

    @GetMapping("/me/problem/{problemId}")
    public ResponseEntity<List<SubmissionResponse>> getMySubmissionsForProblem(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String problemId
    ) {
        return ResponseEntity.ok(submissionService.getUserSubmissionsForProblem(UUID.fromString(userId), problemId));
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<SubmissionResponse> getSubmissionById(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID submissionId
    ) {
        return ResponseEntity.ok(submissionService.getSubmissionById(submissionId, UUID.fromString(userId)));
    }
}
