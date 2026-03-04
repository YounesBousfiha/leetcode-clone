package com.leetcode.userservice.prensetation.dto;

import lombok.Builder;

@Builder
public record UserStatisticsResponse(
        String userId,
        Long totalScore,
        Integer totalProblemsSolved,
        Integer easySolved,
        Integer mediumSolved,
        Integer hardSolved,
        Integer totalSubmissions,
        Integer acceptedSubmissions,
        Double acceptanceRate,
        Integer currentStreak,
        Integer longestStreak
) {
}

