package com.leetcode.userservice.prensetation.controller;


import com.leetcode.userservice.application.service.IUserProfileService;
import com.leetcode.userservice.prensetation.dto.UpdateProfileRequest;
import com.leetcode.userservice.prensetation.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final IUserProfileService userProfileService;


    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.ok(userProfileService.getProfileById(userId));
    }


    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody UpdateProfileRequest request
            ) {
        return ResponseEntity.ok(userProfileService.updateProfile(userId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable("id") String id
    ) {
        log.info("Hello: {}", id);
        return ResponseEntity.ok(userProfileService.getProfileById(id));
    }
}
