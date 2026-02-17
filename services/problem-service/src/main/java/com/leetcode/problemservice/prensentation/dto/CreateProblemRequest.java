package com.leetcode.problemservice.prensentation.dto;

import com.leetcode.problemservice.domain.enums.Difficulty;

import java.util.List;

public record CreateProblemRequest(
        String title,
        String description,
        Difficulty difficulty,
        Double timeLimit,
        Integer memoryLimit,

        List<String> tags,
        List<TestCaseDto> testCase,
        List<CodeTemplateDto> codeTemplate,
        List<String> hints
) {
}
