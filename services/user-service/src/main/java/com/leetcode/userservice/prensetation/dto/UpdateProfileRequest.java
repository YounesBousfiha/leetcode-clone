package com.leetcode.userservice.prensetation.dto;

import lombok.Builder;

@Builder
public record UpdateProfileRequest(
        String displayName,
        String bio,
        String githubUrl,
        String linkedinUrl
) {
}
