package com.leetcode.userservice.prensetation.dto;

import lombok.Builder;

@Builder
public record UserProfileResponse(
        String userId,
        String email,
        String displayName,
        String bio,
        String githubUrl,
        String linkdeinUrl,
        Long score) {
}
