package com.leetcode.problemservice.prensentation.dto;

import java.util.List;

/**
 * Internal DTO for judge-service to retrieve problem details with all test cases.
 * This is separate from ProblemDetailResponse to avoid exposing test cases publicly.
 */
public record InternalProblemResponse(
        String id,
        String slug,
        String difficulty,
        Double timeLimit,
        Integer memoryLimit,
        List<TestCaseDto> testCases
) {
}

