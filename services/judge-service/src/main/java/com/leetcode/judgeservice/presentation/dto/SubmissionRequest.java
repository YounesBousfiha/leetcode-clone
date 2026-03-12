package com.leetcode.judgeservice.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SubmissionRequest(
        @NotBlank(message = "Code cannot be empty")
        String code,

        @NotBlank(message = "Language is required")
        String language,

        @NotBlank(message = "Problem slug is required")
        String problemSlug) {
}
