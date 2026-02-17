package com.leetcode.problemservice.prensentation.dto;

import lombok.Builder;

@Builder
public record TagDto(
        String name,
        String slug) {
}
