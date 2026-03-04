package com.leetcode.userservice.application.service;

import com.leetcode.userservice.prensetation.dto.LeaderboardResponse;
import com.leetcode.userservice.prensetation.dto.UserStatisticsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ILeaderboardService {
    Page<LeaderboardResponse> getGlobalLeaderboard(Pageable pageable);
    UserStatisticsResponse getUserStatistics(String userId);
    void updateUserScore(String userId, Integer points, String difficulty);
}

