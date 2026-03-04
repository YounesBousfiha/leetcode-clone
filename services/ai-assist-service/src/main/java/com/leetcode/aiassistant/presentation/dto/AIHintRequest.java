package com.leetcode.aiassistant.presentation.dto;

import lombok.Builder;

@Builder
public record AIHintRequest(
        String problemSlug,
        String userCode,
        String language
) {
}

