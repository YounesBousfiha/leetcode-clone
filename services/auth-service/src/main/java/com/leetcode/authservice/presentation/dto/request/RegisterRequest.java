package com.leetcode.authservice.presentation.dto.request;

public record RegisterRequest(
        String email,
        String password,
        String displayName
) {
}
