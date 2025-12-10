package com.leetcode.authservice.presentation.dto.request;

import lombok.Builder;

@Builder
public record ResetPasswordRequest(
        String newPassword
) {
}
