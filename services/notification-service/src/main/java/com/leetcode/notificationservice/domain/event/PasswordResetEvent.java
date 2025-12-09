package com.leetcode.notificationservice.domain.event;

public record PasswordResetEvent(
        String email,
        String token,
        String displayName
) {
}