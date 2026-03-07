package com.leetcode.userservice.application.service.impl;

import com.leetcode.userservice.application.mapper.UserProfileMapper;
import com.leetcode.userservice.domain.entity.UserProfile;
import com.leetcode.userservice.domain.event.UserRegisteredEvent;
import com.leetcode.userservice.infrastructure.repository.UserProfileRepository;
import com.leetcode.userservice.prensetation.dto.UpdateProfileRequest;
import com.leetcode.userservice.prensetation.dto.UserProfileResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserProfileMapper mapper;

    @InjectMocks
    private UserProfileService userProfileService;

    private final UUID userId = UUID.randomUUID();

    @Test
    @DisplayName("createNewProfile - should create profile for new user")
    void createNewProfile_shouldCreateProfile() {
        UserRegisteredEvent event = new UserRegisteredEvent(
                userId.toString(), "test@example.com", "TestUser", "token");
        when(userProfileRepository.existsById(userId)).thenReturn(false);

        userProfileService.createNewProfile(event);

        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    @DisplayName("createNewProfile - should skip if profile already exists")
    void createNewProfile_shouldSkipIfExists() {
        UserRegisteredEvent event = new UserRegisteredEvent(
                userId.toString(), "test@example.com", "TestUser", "token");
        when(userProfileRepository.existsById(userId)).thenReturn(true);

        userProfileService.createNewProfile(event);

        verify(userProfileRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateProfile - should update and return profile")
    void updateProfile_shouldUpdateAndReturn() {
        UserProfile profile = UserProfile.builder()
                .id(userId).email("test@example.com").displayName("OldName").score(100L).build();
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .displayName("NewName").bio("New Bio").build();
        UserProfileResponse expectedResponse = UserProfileResponse.builder()
                .userId(userId.toString()).displayName("NewName").email("test@example.com").score(100L).build();

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(profile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(profile);
        when(mapper.toResponse(any(UserProfile.class))).thenReturn(expectedResponse);

        UserProfileResponse result = userProfileService.updateProfile(userId.toString(), request);

        assertThat(result.displayName()).isEqualTo("NewName");
        verify(mapper).updateEntityFromRequest(request, profile);
        verify(userProfileRepository).save(profile);
    }

    @Test
    @DisplayName("updateProfile - should throw when profile not found")
    void updateProfile_shouldThrowWhenNotFound() {
        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.updateProfile(userId.toString(),
                UpdateProfileRequest.builder().build()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Profile not found");
    }

    @Test
    @DisplayName("getProfileById - should return profile")
    void getProfileById_shouldReturnProfile() {
        UserProfile profile = UserProfile.builder()
                .id(userId).email("test@example.com").displayName("TestUser").score(50L).build();
        UserProfileResponse expectedResponse = UserProfileResponse.builder()
                .userId(userId.toString()).displayName("TestUser").email("test@example.com").score(50L).build();

        when(userProfileRepository.findById(userId)).thenReturn(Optional.of(profile));
        when(mapper.toResponse(profile)).thenReturn(expectedResponse);

        UserProfileResponse result = userProfileService.getProfileById(userId.toString());

        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.displayName()).isEqualTo("TestUser");
    }

    @Test
    @DisplayName("getProfileById - should throw when profile not found")
    void getProfileById_shouldThrowWhenNotFound() {
        when(userProfileRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.getProfileById(userId.toString()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Profile not found");
    }
}

