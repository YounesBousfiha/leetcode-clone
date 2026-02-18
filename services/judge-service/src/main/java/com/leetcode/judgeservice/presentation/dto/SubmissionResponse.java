package com.leetcode.judgeservice.presentation.dto;

import com.leetcode.judgeservice.domain.entity.SubmissionResult;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record SubmissionResponse(
        UUID id,
        String status,
        Double executionTime,
        Long MemoryUsed,
        LocalDateTime createdAt,
        List<SubmissionResultResponse> details
        ) {
}
