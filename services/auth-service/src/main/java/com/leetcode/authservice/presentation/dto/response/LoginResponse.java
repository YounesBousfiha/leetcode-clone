package com.leetcode.authservice.presentation.dto.response;

public record LoginResponse(
        String accessToken,
        String userId,
        String email,
        String role
) {
}
