package com.leetcode.userservice.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserStatisticsTest {

    private UserStatistics stats;

    @BeforeEach
    void setUp() {
        stats = UserStatistics.builder()
                .userId(UUID.randomUUID())
                .totalProblemsSolved(0)
                .easySolved(0)
                .mediumSolved(0)
                .hardSolved(0)
                .totalSubmissions(0)
                .acceptedSubmissions(0)
                .currentStreak(0)
                .longestStreak(0)
                .build();
    }

    @Test
    @DisplayName("incrementProblemSolved EASY - should increment easy and total")
    void incrementProblemSolved_easy() {
        stats.incrementProblemSolved("EASY");

        assertThat(stats.getEasySolved()).isEqualTo(1);
        assertThat(stats.getTotalProblemsSolved()).isEqualTo(1);
        assertThat(stats.getMediumSolved()).isZero();
        assertThat(stats.getHardSolved()).isZero();
    }

    @Test
    @DisplayName("incrementProblemSolved MEDIUM - should increment medium")
    void incrementProblemSolved_medium() {
        stats.incrementProblemSolved("MEDIUM");

        assertThat(stats.getMediumSolved()).isEqualTo(1);
        assertThat(stats.getTotalProblemsSolved()).isEqualTo(1);
    }

    @Test
    @DisplayName("incrementProblemSolved HARD - should increment hard")
    void incrementProblemSolved_hard() {
        stats.incrementProblemSolved("HARD");

        assertThat(stats.getHardSolved()).isEqualTo(1);
        assertThat(stats.getTotalProblemsSolved()).isEqualTo(1);
    }

    @Test
    @DisplayName("incrementSubmissions - should increment total submissions")
    void incrementSubmissions() {
        stats.incrementSubmissions();
        stats.incrementSubmissions();

        assertThat(stats.getTotalSubmissions()).isEqualTo(2);
    }

    @Test
    @DisplayName("incrementAcceptedSubmissions - should increment accepted")
    void incrementAcceptedSubmissions() {
        stats.incrementAcceptedSubmissions();

        assertThat(stats.getAcceptedSubmissions()).isEqualTo(1);
    }

    @Test
    @DisplayName("getAcceptanceRate - should calculate correct rate")
    void getAcceptanceRate_shouldCalculate() {
        stats.setTotalSubmissions(10);
        stats.setAcceptedSubmissions(7);

        assertThat(stats.getAcceptanceRate()).isEqualTo(70.0);
    }

    @Test
    @DisplayName("getAcceptanceRate - should return 0 when no submissions")
    void getAcceptanceRate_shouldReturnZero() {
        assertThat(stats.getAcceptanceRate()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("incrementProblemSolved - should start streak on first solve")
    void incrementProblemSolved_shouldStartStreak() {
        stats.incrementProblemSolved("EASY");

        assertThat(stats.getCurrentStreak()).isEqualTo(1);
        assertThat(stats.getLongestStreak()).isEqualTo(1);
    }
}

