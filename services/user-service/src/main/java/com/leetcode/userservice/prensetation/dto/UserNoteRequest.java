package com.leetcode.userservice.prensetation.dto;

public record UserNoteRequest(
        String problemId,
        String content
        ) {
}
