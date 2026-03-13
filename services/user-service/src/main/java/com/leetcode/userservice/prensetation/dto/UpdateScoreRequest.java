package com.leetcode.userservice.prensetation.dto;

public record UpdateScoreRequest(
        String userId,
        Integer points,
        String difficulty
) {
}

