package com.leetcode.judgeservice.infrastructure.client.dto;

import java.util.List;

public record ProblemDetailResponse(
        String id,
        String slug,
        Double timeLimit,
        Integer memoryLimit,
        List<TestCaseDto> testCases
) {
}
