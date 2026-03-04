package com.leetcode.aiassistant.presentation.dto;

import lombok.Builder;

@Builder
public record AIHintResponse(
        String hint,
        String complexity,
        String approach
) {
}

