package com.leetcode.userservice.prensetation.controller;


import com.leetcode.userservice.application.service.IUserProfileService;
import com.leetcode.userservice.prensetation.dto.UpdateProfileRequest;
import com.leetcode.userservice.prensetation.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Profiles", description = "User profile management endpoints")
public class UserController {

    private final IUserProfileService userProfileService;


    @GetMapping("/me")
    @Operation(summary = "Get my profile", description = "Get the authenticated user's profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.ok(userProfileService.getProfileById(userId));
    }


    @PutMapping("/me")
    @Operation(summary = "Update my profile", description = "Update the authenticated user's profile")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String userId,
            @RequestBody UpdateProfileRequest request
            ) {
        return ResponseEntity.ok(userProfileService.updateProfile(userId, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user profile", description = "Get a public user profile by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @Parameter(description = "User ID") @PathVariable("id") String id
    ) {
        log.info("Hello: {}", id);
        return ResponseEntity.ok(userProfileService.getProfileById(id));
    }
}
