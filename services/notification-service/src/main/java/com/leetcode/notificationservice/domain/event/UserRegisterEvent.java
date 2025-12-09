package com.leetcode.notificationservice.domain.event;


import lombok.Builder;

@Builder
public record UserRegisterEvent(
        String userId,
        String email,
        String displayName,
        String verificationToken
) {
}
