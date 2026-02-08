package com.leetcode.userservice.application.service;

import com.leetcode.userservice.domain.entity.UserNote;
import com.leetcode.userservice.prensetation.dto.UserNoteRequest;
import com.leetcode.userservice.prensetation.dto.UserNoteResponse;

public interface IUserNoteService {
    UserNoteResponse saveNote(String userId, UserNoteRequest note);
    UserNoteResponse getNote(String userId, String problemId);
}
