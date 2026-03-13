package com.leetcode.userservice.prensetation.controller;

import com.leetcode.userservice.application.service.ILeaderboardService;
import com.leetcode.userservice.prensetation.dto.UpdateScoreRequest;
import com.leetcode.userservice.prensetation.dto.LeaderboardResponse;
import com.leetcode.userservice.prensetation.dto.UserStatisticsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Leaderboard", description = "Global ranking and user statistics endpoints")
public class LeaderboardController {

    private final ILeaderboardService leaderboardService;

    @GetMapping
    @Operation(summary = "Get global leaderboard", description = "Get paginated global leaderboard sorted by score")
    @ApiResponse(responseCode = "200", description = "Leaderboard retrieved successfully")
    public ResponseEntity<Page<LeaderboardResponse>> getGlobalLeaderboard(
            @PageableDefault(size = 50, sort = "score") Pageable pageable
    ) {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard(pageable));
    }

    @GetMapping("/statistics/{userId}")
    @Operation(summary = "Get user statistics", description = "Get detailed statistics for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserStatisticsResponse> getUserStatistics(
            @Parameter(description = "User ID") @PathVariable String userId
    ) {
        return ResponseEntity.ok(leaderboardService.getUserStatistics(userId));
    }

    @GetMapping("/statistics/me")
    @Operation(summary = "Get my statistics", description = "Get the authenticated user's statistics")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public ResponseEntity<UserStatisticsResponse> getMyStatistics(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.ok(leaderboardService.getUserStatistics(userId));
    }

    @PostMapping("/internal/update-score")
    @Operation(summary = "Internal score update", description = "Internal endpoint for judge-service to update user score and solved stats")
    @ApiResponse(responseCode = "204", description = "Score updated successfully")
    public ResponseEntity<Void> updateScoreInternal(@RequestBody UpdateScoreRequest request) {
        leaderboardService.updateUserScore(request.userId(), request.points(), request.difficulty());
        return ResponseEntity.noContent().build();
    }
}

