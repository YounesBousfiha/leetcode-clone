package com.leetcode.judgeservice.infrastructure.client.dto;

public record ProblemDetailResponse(
        String id,
        String slug,
        Double timeLimit,
        Integer memoryLimit,
        List<TestCaseDto> testCases
) {
}
