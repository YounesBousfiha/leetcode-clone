package com.leetcode.aiassistant.presentation.dto;

import lombok.Builder;

@Builder
public record CodeReviewRequest(
        String code,
        String language,
        String problemSlug
) {
}

