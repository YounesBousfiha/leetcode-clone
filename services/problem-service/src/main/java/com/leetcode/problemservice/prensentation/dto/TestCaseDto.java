package com.leetcode.problemservice.prensentation.dto;

import lombok.Builder;

@Builder
public record TestCaseDto(
        String input,
        String expectedOutput,
        boolean isPublic) {
}
