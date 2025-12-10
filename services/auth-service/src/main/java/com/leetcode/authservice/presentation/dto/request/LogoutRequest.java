package com.leetcode.authservice.presentation.dto.request;

public record LogoutRequest(
        String refreshToken
) {
}
