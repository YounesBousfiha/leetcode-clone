package com.leetcode.userservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user_statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatistics {

    @Id
    private UUID userId;

    @Builder.Default
    private Integer totalProblemsSolved = 0;

    @Builder.Default
    private Integer easySolved = 0;

    @Builder.Default
    private Integer mediumSolved = 0;

    @Builder.Default
    private Integer hardSolved = 0;

    @Builder.Default
    private Integer totalSubmissions = 0;

    @Builder.Default
    private Integer acceptedSubmissions = 0;

    @Builder.Default
    private Integer currentStreak = 0;

    @Builder.Default
    private Integer longestStreak = 0;

    private LocalDate lastSolvedDate;

    @CreationTimestamp
    private LocalDate createdAt;

    public void incrementProblemSolved(String difficulty) {
        this.totalProblemsSolved++;

        if (difficulty != null) {
            switch (difficulty.toUpperCase()) {
                case "EASY" -> this.easySolved++;
                case "MEDIUM" -> this.mediumSolved++;
                case "HARD" -> this.hardSolved++;
                default -> {
                    // Unknown difficulty: keep solved count but skip bucket increment.
                }
            }
        }

        updateStreak();
    }

    public void incrementSubmissions() {
        this.totalSubmissions++;
    }

    public void incrementAcceptedSubmissions() {
        this.acceptedSubmissions++;
    }

    private void updateStreak() {
        LocalDate today = LocalDate.now();
        if (lastSolvedDate == null) {
            currentStreak = 1;
            lastSolvedDate = today;
        } else if (lastSolvedDate.equals(today)) {
            // Même jour, ne rien faire
            return;
        } else if (lastSolvedDate.equals(today.minusDays(1))) {
            // Jour consécutif
            currentStreak++;
            lastSolvedDate = today;
        } else {
            // Streak cassé
            currentStreak = 1;
            lastSolvedDate = today;
        }

        if (currentStreak > longestStreak) {
            longestStreak = currentStreak;
        }
    }

    public Double getAcceptanceRate() {
        if (totalSubmissions == 0) return 0.0;
        return (acceptedSubmissions * 100.0) / totalSubmissions;
    }
}

