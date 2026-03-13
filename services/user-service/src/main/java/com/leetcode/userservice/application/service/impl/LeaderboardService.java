package com.leetcode.userservice.application.service.impl;

import com.leetcode.userservice.application.service.ILeaderboardService;
import com.leetcode.userservice.domain.entity.UserProfile;
import com.leetcode.userservice.domain.entity.UserStatistics;
import com.leetcode.userservice.infrastructure.repository.UserProfileRepository;
import com.leetcode.userservice.infrastructure.repository.UserStatisticsRepository;
import com.leetcode.userservice.prensetation.dto.LeaderboardResponse;
import com.leetcode.userservice.prensetation.dto.UserStatisticsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardService implements ILeaderboardService {

    private final UserProfileRepository userProfileRepository;
    private final UserStatisticsRepository userStatisticsRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<LeaderboardResponse> getGlobalLeaderboard(Pageable pageable) {
        Page<UserProfile> profiles = userProfileRepository.findAll(pageable);

        List<LeaderboardResponse> leaderboard = profiles.getContent().stream()
                .sorted((p1, p2) -> Long.compare(p2.getScore(), p1.getScore()))
                .map(profile -> {
                    UserStatistics stats = userStatisticsRepository.findById(profile.getId())
                            .orElse(UserStatistics.builder()
                                    .userId(profile.getId())
                                    .build());

                    return LeaderboardResponse.builder()
                            .userId(profile.getId().toString())
                            .displayName(profile.getDisplayName())
                            .score(profile.getScore())
                            .rank(0) // Will be calculated after sorting
                            .problemsSolved(stats.getTotalProblemsSolved())
                            .easySolved(stats.getEasySolved())
                            .mediumSolved(stats.getMediumSolved())
                            .hardSolved(stats.getHardSolved())
                            .build();
                })
                .collect(Collectors.toList());

        // Assign ranks
        for (int i = 0; i < leaderboard.size(); i++) {
            LeaderboardResponse entry = leaderboard.get(i);
            leaderboard.set(i, LeaderboardResponse.builder()
                    .userId(entry.userId())
                    .displayName(entry.displayName())
                    .score(entry.score())
                    .rank(i + 1 + (int) pageable.getOffset())
                    .problemsSolved(entry.problemsSolved())
                    .easySolved(entry.easySolved())
                    .mediumSolved(entry.mediumSolved())
                    .hardSolved(entry.hardSolved())
                    .build());
        }

        return new PageImpl<>(leaderboard, pageable, profiles.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatisticsResponse getUserStatistics(String userId) {
        UUID id = UUID.fromString(userId);
        UserProfile profile = userProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        UserStatistics stats = userStatisticsRepository.findById(id)
                .orElse(UserStatistics.builder()
                        .userId(id)
                        .build());

        return UserStatisticsResponse.builder()
                .userId(userId)
                .totalScore(profile.getScore())
                .totalProblemsSolved(stats.getTotalProblemsSolved())
                .easySolved(stats.getEasySolved())
                .mediumSolved(stats.getMediumSolved())
                .hardSolved(stats.getHardSolved())
                .totalSubmissions(stats.getTotalSubmissions())
                .acceptedSubmissions(stats.getAcceptedSubmissions())
                .acceptanceRate(stats.getAcceptanceRate())
                .currentStreak(stats.getCurrentStreak())
                .longestStreak(stats.getLongestStreak())
                .build();
    }

    @Override
    @Transactional
    public void updateUserScore(String userId, Integer points, String difficulty) {
        UUID id = UUID.fromString(userId);
        int safePoints = points != null ? points : 0;

        // Update profile score
        UserProfile profile = userProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        profile.setScore(profile.getScore() + safePoints);
        userProfileRepository.save(profile);

        // Update statistics
        UserStatistics stats = userStatisticsRepository.findById(id)
                .orElse(UserStatistics.builder()
                        .userId(id)
                        .build());

        stats.incrementProblemSolved(difficulty);
        stats.incrementSubmissions();
        stats.incrementAcceptedSubmissions();
        userStatisticsRepository.save(stats);

        log.info("Updated score for user {} with {} points ({})", userId, safePoints, difficulty);
    }
}
