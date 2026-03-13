package com.leetcode.userservice.application.service.impl;

import com.leetcode.userservice.domain.entity.UserProfile;
import com.leetcode.userservice.domain.entity.UserStatistics;
import com.leetcode.userservice.infrastructure.repository.UserProfileRepository;
import com.leetcode.userservice.infrastructure.repository.UserStatisticsRepository;
import com.leetcode.userservice.prensetation.dto.LeaderboardResponse;
import com.leetcode.userservice.prensetation.dto.UserStatisticsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserStatisticsRepository userStatisticsRepository;

    @InjectMocks
    private LeaderboardService leaderboardService;

    private final UUID userId = UUID.randomUUID();

    @Test
    @DisplayName("getGlobalLeaderboard - should return sorted leaderboard with ranks")
    void getGlobalLeaderboard_shouldReturnSortedLeaderboard() {
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();
        UserProfile profile1 = UserProfile.builder().id(user1Id).displayName("User1").score(200L).build();
        UserProfile profile2 = UserProfile.builder().id(user2Id).displayName("User2").score(100L).build();
        UserStatistics stats1 = UserStatistics.builder().userId(user1Id).totalProblemsSolved(10)
                .easySolved(5).mediumSolved(3).hardSolved(2).build();
        UserStatistics stats2 = UserStatistics.builder().userId(user2Id).totalProblemsSolved(5)
                .easySolved(3).mediumSolved(2).hardSolved(0).build();

        Pageable pageable = PageRequest.of(0, 50);
        Page<UserProfile> profilePage = new PageImpl<>(List.of(profile1, profile2), pageable, 2);

        when(userProfileRepository.findAll(pageable)).thenReturn(profilePage);
        when(userStatisticsRepository.findById(user1Id)).thenReturn(Optional.of(stats1));
        when(userStatisticsRepository.findById(user2Id)).thenReturn(Optional.of(stats2));

        Page<LeaderboardResponse> result = leaderboardService.getGlobalLeaderboard(pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).score()).isEqualTo(200L);
        assertThat(result.getContent().get(0).rank()).isEqualTo(1);
        assertThat(result.getContent().get(1).score()).isEqualTo(100L);
        assertThat(result.getContent().get(1).rank()).isEqualTo(2);
    }

    @Test
    @DisplayName("getGlobalLeaderboard - should handle missing statistics")
    void getGlobalLeaderboard_shouldHandleMissingStats() {
        UserProfile profile = UserProfile.builder().id(userId).displayName("User1").score(50L).build();
        Pageable pageable = PageRequest.of(0, 50);
        Page<UserProfile> profilePage = new PageImpl<>(List.of(profile), pageable, 1);

        when(userProfileRepository.findAll(pageable)).thenReturn(profilePage);
        when(userStatisticsRepository.findById(userId)).thenReturn(Optional.empty());

        Page<LeaderboardResponse> result = leaderboardService.getGlobalLeaderboard(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).problemsSolved()).isEqualTo(0);
    }

    @Test
    @DisplayName("getUserStatistics - should return statistics for user")
    void getUserStatistics_shouldReturnStats() {
        UserProfile profile = UserProfile.builder().id(userId).score(300L).build();
        UserStatistics stats = UserStatistics.builder()
                .userId(userId).totalProblemsSolved(15).easySolved(8).mediumSolved(5).hardSolved(2)
                .totalSubmissions(30).acceptedSubmissions(20).currentStreak(5).longestStreak(10).build();

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(profile));
        when(userStatisticsRepository.findById(userId)).thenReturn(Optional.of(stats));

        UserStatisticsResponse result = leaderboardService.getUserStatistics(userId.toString());

        assertThat(result.totalScore()).isEqualTo(300L);
        assertThat(result.totalProblemsSolved()).isEqualTo(15);
        assertThat(result.currentStreak()).isEqualTo(5);
        assertThat(result.longestStreak()).isEqualTo(10);
    }

    @Test
    @DisplayName("getUserStatistics - should throw when profile not found")
    void getUserStatistics_shouldThrowWhenNotFound() {
        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leaderboardService.getUserStatistics(userId.toString()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User profile not found");
    }

    @Test
    @DisplayName("updateUserScore - should increment score and update statistics")
    void updateUserScore_shouldIncrementScore() {
        UserProfile profile = UserProfile.builder().id(userId).score(100L).build();
        UserStatistics stats = UserStatistics.builder()
                .userId(userId).totalProblemsSolved(5).easySolved(3).mediumSolved(2).hardSolved(0)
                .totalSubmissions(10).acceptedSubmissions(6).build();

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(profile));
        when(userStatisticsRepository.findById(userId)).thenReturn(Optional.of(stats));

        leaderboardService.updateUserScore(userId.toString(), 50, "HARD");

        assertThat(profile.getScore()).isEqualTo(150L);
        assertThat(stats.getTotalSubmissions()).isEqualTo(11);
        assertThat(stats.getAcceptedSubmissions()).isEqualTo(7);
        verify(userProfileRepository).save(profile);
        verify(userStatisticsRepository).save(stats);
    }

    @Test
    @DisplayName("updateUserScore - should create statistics if not exists")
    void updateUserScore_shouldCreateStatsIfNotExists() {
        UserProfile profile = UserProfile.builder().id(userId).score(0L).build();

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(profile));
        when(userStatisticsRepository.findById(userId)).thenReturn(Optional.empty());

        leaderboardService.updateUserScore(userId.toString(), 10, "EASY");

        verify(userProfileRepository).save(profile);
        verify(userStatisticsRepository).save(any(UserStatistics.class));
        assertThat(profile.getScore()).isEqualTo(10L);
    }

    @Test
    @DisplayName("updateUserScore - should throw when profile not found")
    void updateUserScore_shouldThrowWhenProfileNotFound() {
        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leaderboardService.updateUserScore(userId.toString(), 10, "EASY"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User profile not found");
    }
}

