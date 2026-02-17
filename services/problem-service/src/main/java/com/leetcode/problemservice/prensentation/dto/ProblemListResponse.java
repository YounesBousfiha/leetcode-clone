package com.leetcode.problemservice.prensentation.dto;

import com.leetcode.problemservice.domain.enums.Difficulty;

import java.util.Set;

public record ProblemListResponse(
        String id,
        String title,
        String slug,
        Difficulty difficulty,
        Set<TagDto> tags
) {
}
