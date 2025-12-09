package com.leetcode.authservice.domain.event;

import lombok.Builder;

@Builder
public record PasswordResetEvent(
        String email,
        String token,
        String displayName
) {
}
