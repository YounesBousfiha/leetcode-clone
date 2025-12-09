package com.leetcode.userservice.domain.event;

public record UserRegisteredEvent(
        String userId,
        String email,
        String displayName,
        String verificationToken
) {
}
