package com.leetcode.authservice.presentation.dto.request;

public record LoginRequest(
        String email,
        String password
) {
}
