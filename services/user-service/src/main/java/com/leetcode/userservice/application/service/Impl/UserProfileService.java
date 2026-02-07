package com.leetcode.userservice.application.service.Impl;


import com.leetcode.userservice.application.mapper.UserProfileMapper;
import com.leetcode.userservice.application.service.IUserProfileService;
import com.leetcode.userservice.domain.entity.UserProfile;
import com.leetcode.userservice.domain.event.UserRegisteredEvent;
import com.leetcode.userservice.infrastructure.repository.UserProfileRepository;
import com.leetcode.userservice.prensetation.dto.UpdateProfileRequest;
import com.leetcode.userservice.prensetation.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService implements IUserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper mapper;

    @Override
    @Transactional
    public void createNewProfile(UserRegisteredEvent event) {

        log.info("Creating profile for user: {}", event.userId());

        if(userProfileRepository.existsById(UUID.fromString(event.userId()))) {
            log.warn("Profile already exists for user ID : {}", event.userId());
            return;
        }

        UserProfile newProfile = UserProfile.builder()
                .id(UUID.fromString(event.userId()))
                .email(event.email())
                .displayName(event.displayName())
                .score(0L)
                .build();

        this.userProfileRepository.save(newProfile);
        log.info("Profile created successfully for user : {}", event.email());
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(String id, UpdateProfileRequest request) {
        UserProfile profile = userProfileRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        mapper.updateEntityFromRequest(request, profile);

        UserProfile savedProfile = userProfileRepository.save(profile);

        return mapper.toResponse(savedProfile);

    }

    @Override
    public UserProfileResponse getProfileById(String id) {
        UserProfile profile = userProfileRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        log.info("Profile ID: {}", profile.getId());
        return mapper.toResponse(profile);
    }
}
