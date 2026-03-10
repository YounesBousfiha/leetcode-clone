package com.leetcode.userservice.infrastructure.seeder;

import com.leetcode.userservice.domain.entity.UserProfile;
import com.leetcode.userservice.domain.entity.UserStatistics;
import com.leetcode.userservice.infrastructure.repository.UserProfileRepository;
import com.leetcode.userservice.infrastructure.repository.UserStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"dev", "local"})
public class UserProfileSeeder implements CommandLineRunner {

    private final UserProfileRepository userProfileRepository;
    private final UserStatisticsRepository userStatisticsRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userProfileRepository.count() > 0) {
            log.info("✅ User profiles already seeded");
            return;
        }

        log.info("🌱 Seeding user profiles and statistics...");

        seedUserProfiles();

        log.info("✅ User profiles seeding completed!");
    }

    private void seedUserProfiles() {
        List<UserProfile> profiles = new ArrayList<>();
        List<UserStatistics> statistics = new ArrayList<>();

        // Admin profile
        UUID adminId = UUID.randomUUID();
        profiles.add(createUserProfile(adminId, "admin@codemasters.com", "Root Admin",
                "System administrator and problem curator",
                "https://github.com/admin", "https://linkedin.com/in/admin", 5000L));
        statistics.add(createUserStatistics(adminId, 50, 20, 20, 10));

        // Test users with varying levels of activity
        String[][] userData = {
            {"john.doe@example.com", "John Doe", "Full-stack developer passionate about algorithms",
             "https://github.com/johndoe", "https://linkedin.com/in/johndoe"},
            {"jane.smith@example.com", "Jane Smith", "Software engineer and competitive programmer",
             "https://github.com/janesmith", "https://linkedin.com/in/janesmith"},
            {"alice.wonder@example.com", "Alice Wonder", "CS student learning data structures",
             "", ""},
            {"bob.builder@example.com", "Bob Builder", "Backend developer focusing on system design",
             "https://github.com/bobbuilder", ""},
            {"charlie.brown@example.com", "Charlie Brown", "Frontend developer exploring algorithms",
             "", "https://linkedin.com/in/charliebrown"}
        };

        int[][] activityData = {
            {25, 10, 10, 5},  // John: 25 total, 10 easy, 10 medium, 5 hard
            {40, 15, 20, 5},  // Jane: 40 total, 15 easy, 20 medium, 5 hard
            {10, 8, 2, 0},    // Alice: 10 total, 8 easy, 2 medium, 0 hard
            {30, 8, 15, 7},   // Bob: 30 total, 8 easy, 15 medium, 7 hard
            {15, 10, 5, 0}    // Charlie: 15 total, 10 easy, 5 medium, 0 hard
        };

        long[] scores = {2500L, 4000L, 800L, 3200L, 1500L};

        for (int i = 0; i < userData.length; i++) {
            UUID userId = UUID.randomUUID();
            String[] user = userData[i];
            int[] activity = activityData[i];

            profiles.add(createUserProfile(userId, user[0], user[1], user[2], user[3], user[4], scores[i]));
            statistics.add(createUserStatistics(userId, activity[0], activity[1], activity[2], activity[3]));
        }

        userProfileRepository.saveAll(profiles);
        userStatisticsRepository.saveAll(statistics);

        log.info("✅ Seeded {} user profiles and statistics", profiles.size());
    }

    private UserProfile createUserProfile(UUID id, String email, String displayName,
                                          String bio, String githubUrl, String linkedinUrl, Long score) {
        return UserProfile.builder()
                .id(id)
                .email(email)
                .displayName(displayName)
                .bio(bio)
                .githubUrl(githubUrl.isEmpty() ? null : githubUrl)
                .linkedinUrl(linkedinUrl.isEmpty() ? null : linkedinUrl)
                .score(score)
                .build();
    }

    private UserStatistics createUserStatistics(UUID userId, int totalSolved,
                                                int easySolved, int mediumSolved, int hardSolved) {
        int totalSubmissions = totalSolved * 3; // Assuming ~3 submissions per solved problem
        int acceptedSubmissions = totalSolved;
        int currentStreak = (int) (Math.random() * 10); // Random streak 0-9
        int longestStreak = currentStreak + (int) (Math.random() * 10); // Longest is at least current

        return UserStatistics.builder()
                .userId(userId)
                .totalProblemsSolved(totalSolved)
                .easySolved(easySolved)
                .mediumSolved(mediumSolved)
                .hardSolved(hardSolved)
                .totalSubmissions(totalSubmissions)
                .acceptedSubmissions(acceptedSubmissions)
                .currentStreak(currentStreak)
                .longestStreak(longestStreak)
                .build();
    }
}

