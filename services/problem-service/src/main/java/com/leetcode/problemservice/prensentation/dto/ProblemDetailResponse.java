package com.leetcode.problemservice.prensentation.dto;

import com.leetcode.problemservice.domain.enums.Difficulty;

import java.util.List;
import java.util.Set;

public record ProblemDetailResponse(
        String id,
        String title,
        String slug,
        String description,
        Difficulty difficulty,
        Double timeLimit,
        Integer memoryLimit,
        Set<TagDto> tags,
        List<TestCaseDto> examples,
        List<CodeTemplateDto> templates,
        List<String> hints
) {
}
