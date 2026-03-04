package com.leetcode.aiassistant.presentation.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CodeReviewResponse(
        String overallFeedback,
        List<String> improvements,
        List<String> bugs,
        Integer codeQualityScore
) {
}

