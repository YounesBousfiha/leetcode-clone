package com.leetcode.userservice.application.service;

import com.leetcode.userservice.domain.entity.UserNote;
import com.leetcode.userservice.prensetation.dto.UserNoteRequest;
import com.leetcode.userservice.prensetation.dto.UserNoteResponse;

public interface IUserNoteService {
    UserNoteResponse save(String userId, UserNoteRequest note);
}
