package com.leetcode.authservice.presentation.dto.response;


import lombok.Builder;

@Builder
public record LoginResponse(
        String accessToken,
        String userId,
        String email,
        String role
) {
}
