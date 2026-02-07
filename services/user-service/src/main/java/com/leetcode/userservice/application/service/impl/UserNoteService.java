package com.leetcode.userservice.application.service.impl;

import com.leetcode.userservice.application.mapper.UserNoteMapper;
import com.leetcode.userservice.application.service.IUserNoteService;
import com.leetcode.userservice.domain.entity.UserNote;
import com.leetcode.userservice.domain.entity.UserProfile;
import com.leetcode.userservice.infrastructure.repository.UserNoteRepository;
import com.leetcode.userservice.infrastructure.repository.UserProfileRepository;
import com.leetcode.userservice.prensetation.dto.UserNoteRequest;
import com.leetcode.userservice.prensetation.dto.UserNoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserNoteService  implements IUserNoteService {

    private final UserProfileRepository userProfileRepository;
    private final UserNoteRepository userNoteRepository;
    private final UserNoteMapper mapper;

    @Override
    public UserNoteResponse save(String userId, UserNoteRequest request) {
        UserProfile user = userProfileRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserNote note = userNoteRepository.findByUserProfileIdAndProblemId(user.getId(), request.problemId())
                .orElse(UserNote.builder()
                        .userProfile(user)
                        .problemId(request.problemId())
                        .build());

        note.setContent(request.content);

        UserNote newNote = userNoteRepository.save(note);

        return mapper.toResponse(newNote);

    }
}
