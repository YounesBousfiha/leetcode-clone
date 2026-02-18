package com.leetcode.judgeservice.presentation.dto;

import lombok.Builder;

@Builder
public record SubmissionResultResponse(
        String testCaseId,
        String output,
        String expectedOutput,
        String errorMessage,
        Double executionTime) {
}
