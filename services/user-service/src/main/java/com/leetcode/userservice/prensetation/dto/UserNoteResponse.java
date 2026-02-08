package com.leetcode.userservice.prensetation.dto;

import java.time.LocalDateTime;

public record UserNoteResponse(
        String problemId,
        String content,
        LocalDateTime lastUpdated) {
}
