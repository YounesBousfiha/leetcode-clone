package com.leetcode.authservice.domain.event;

public record UserRegisteredEvent(
        String userId,
        String email,
        String displayName,
        String VerificationToken
) {
}
