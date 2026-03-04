package com.leetcode.userservice.prensetation.controller;

import com.leetcode.userservice.application.service.ILeaderboardService;
import com.leetcode.userservice.prensetation.dto.LeaderboardResponse;
import com.leetcode.userservice.prensetation.dto.UserStatisticsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
@Slf4j
public class LeaderboardController {

    private final ILeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<Page<LeaderboardResponse>> getGlobalLeaderboard(
            @PageableDefault(size = 50, sort = "score") Pageable pageable
    ) {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard(pageable));
    }

    @GetMapping("/statistics/{userId}")
    public ResponseEntity<UserStatisticsResponse> getUserStatistics(
            @PathVariable String userId
    ) {
        return ResponseEntity.ok(leaderboardService.getUserStatistics(userId));
    }

    @GetMapping("/statistics/me")
    public ResponseEntity<UserStatisticsResponse> getMyStatistics(
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.ok(leaderboardService.getUserStatistics(userId));
    }
}

