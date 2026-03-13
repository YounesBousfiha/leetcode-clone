package com.leetcode.judgeservice.infrastructure.client.dto;

public record UpdateScoreRequest(
        String userId,
        Integer points,
        String difficulty
) {
}

