package com.leetcode.userservice.application.service.impl;

import com.leetcode.userservice.application.mapper.UserNoteMapper;
import com.leetcode.userservice.domain.entity.UserNote;
import com.leetcode.userservice.domain.entity.UserProfile;
import com.leetcode.userservice.infrastructure.repository.UserNoteRepository;
import com.leetcode.userservice.infrastructure.repository.UserProfileRepository;
import com.leetcode.userservice.prensetation.dto.UserNoteRequest;
import com.leetcode.userservice.prensetation.dto.UserNoteResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserNoteServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserNoteRepository userNoteRepository;

    @Mock
    private UserNoteMapper mapper;

    @InjectMocks
    private UserNoteService userNoteService;

    private final UUID userId = UUID.randomUUID();

    @Test
    @DisplayName("saveNote - should create new note when none exists")
    void saveNote_shouldCreateNewNote() {
        UserProfile user = UserProfile.builder().id(userId).build();
        UserNoteRequest request = new UserNoteRequest("problem-1", "My solution notes");
        UserNote savedNote = UserNote.builder()
                .id(UUID.randomUUID()).problemId("problem-1").content("My solution notes")
                .userProfile(user).lastUpdated(LocalDateTime.now()).build();
        UserNoteResponse expectedResponse = new UserNoteResponse("problem-1", "My solution notes", LocalDateTime.now());

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userNoteRepository.findByUserProfileIdAndProblemId(userId, "problem-1")).thenReturn(Optional.empty());
        when(userNoteRepository.save(any(UserNote.class))).thenReturn(savedNote);
        when(mapper.toResponse(savedNote)).thenReturn(expectedResponse);

        UserNoteResponse result = userNoteService.saveNote(userId.toString(), request);

        assertThat(result.problemId()).isEqualTo("problem-1");
        assertThat(result.content()).isEqualTo("My solution notes");
        verify(mapper).updateEntity(eq(request), any(UserNote.class));
    }

    @Test
    @DisplayName("saveNote - should update existing note")
    void saveNote_shouldUpdateExistingNote() {
        UserProfile user = UserProfile.builder().id(userId).build();
        UserNoteRequest request = new UserNoteRequest("problem-1", "Updated notes");
        UserNote existingNote = UserNote.builder()
                .id(UUID.randomUUID()).problemId("problem-1").content("Old notes")
                .userProfile(user).build();
        UserNote savedNote = UserNote.builder()
                .id(existingNote.getId()).problemId("problem-1").content("Updated notes")
                .userProfile(user).build();
        UserNoteResponse expectedResponse = new UserNoteResponse("problem-1", "Updated notes", LocalDateTime.now());

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userNoteRepository.findByUserProfileIdAndProblemId(userId, "problem-1")).thenReturn(Optional.of(existingNote));
        when(userNoteRepository.save(any(UserNote.class))).thenReturn(savedNote);
        when(mapper.toResponse(savedNote)).thenReturn(expectedResponse);

        UserNoteResponse result = userNoteService.saveNote(userId.toString(), request);

        assertThat(result.content()).isEqualTo("Updated notes");
        verify(mapper).updateEntity(request, existingNote);
    }

    @Test
    @DisplayName("saveNote - should throw when user not found")
    void saveNote_shouldThrowWhenUserNotFound() {
        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userNoteService.saveNote(userId.toString(),
                new UserNoteRequest("p1", "notes")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    @DisplayName("getNote - should return note when it exists")
    void getNote_shouldReturnNote() {
        UserNote note = UserNote.builder()
                .problemId("problem-1").content("My notes").build();
        UserNoteResponse expectedResponse = new UserNoteResponse("problem-1", "My notes", LocalDateTime.now());

        when(userNoteRepository.findByUserProfileIdAndProblemId(userId, "problem-1"))
                .thenReturn(Optional.of(note));
        when(mapper.toResponse(note)).thenReturn(expectedResponse);

        UserNoteResponse result = userNoteService.getNote(userId.toString(), "problem-1");

        assertThat(result).isNotNull();
        assertThat(result.problemId()).isEqualTo("problem-1");
    }

    @Test
    @DisplayName("getNote - should return null when note does not exist")
    void getNote_shouldReturnNullWhenNotFound() {
        when(userNoteRepository.findByUserProfileIdAndProblemId(userId, "problem-1"))
                .thenReturn(Optional.empty());

        UserNoteResponse result = userNoteService.getNote(userId.toString(), "problem-1");

        assertThat(result).isNull();
    }
}

