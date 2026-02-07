package com.leetcode.userservice.application.service;

import com.leetcode.userservice.domain.entity.UserProfile;
import com.leetcode.userservice.domain.event.UserRegisteredEvent;

public interface IUserProfileService {

    void createNewProfile(UserRegisteredEvent data);
}
