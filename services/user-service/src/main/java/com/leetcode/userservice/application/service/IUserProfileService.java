package com.leetcode.userservice.application.service;

import com.leetcode.userservice.domain.entity.UserProfile;
import com.leetcode.userservice.domain.event.UserRegisteredEvent;
import com.leetcode.userservice.prensetation.dto.UpdateProfileRequest;
import com.leetcode.userservice.prensetation.dto.UserProfileResponse;

public interface IUserProfileService {

    void createNewProfile(UserRegisteredEvent data);
    UserProfileResponse updateProfile(String id, UpdateProfileRequest request);
    public UserProfileResponse getProfileById(String id);
}
