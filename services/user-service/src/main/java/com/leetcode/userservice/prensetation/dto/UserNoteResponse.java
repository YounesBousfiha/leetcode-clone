package com.leetcode.userservice.prensetation.dto;

import java.time.LocalDateTime;

public record UserNoteResponse(
        String id,
        String problemId,
        String content,
        long user_id,
        LocalDateTime lastUpdated) {
}
