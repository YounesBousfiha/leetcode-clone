package com.leetcode.userservice.prensetation.dto;

import lombok.Builder;

@Builder
public record LeaderboardResponse(
        String userId,
        String displayName,
        Long score,
        Integer rank,
        Integer problemsSolved,
        Integer easySolved,
        Integer mediumSolved,
        Integer hardSolved
) {
}

