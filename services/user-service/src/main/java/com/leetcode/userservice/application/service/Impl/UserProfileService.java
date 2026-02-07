package com.leetcode.userservice.application.service.Impl;


import com.leetcode.userservice.application.service.IUserProfileService;
import com.leetcode.userservice.domain.entity.UserProfile;
import com.leetcode.userservice.domain.event.UserRegisteredEvent;
import com.leetcode.userservice.infrastructure.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService implements IUserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Override
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
}
