package com.leetcode.judgeservice.presentation.controller;


import com.leetcode.judgeservice.application.service.SubmissionService;
import com.leetcode.judgeservice.presentation.dto.SubmissionRequest;
import com.leetcode.judgeservice.presentation.dto.SubmissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
