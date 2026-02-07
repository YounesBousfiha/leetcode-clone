package com.leetcode.userservice.application.mapper;


import com.leetcode.userservice.domain.entity.UserProfile;
import com.leetcode.userservice.domain.event.UserRegisteredEvent;
import com.leetcode.userservice.prensetation.dto.UpdateProfileRequest;
import com.leetcode.userservice.prensetation.dto.UserProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserProfileMapper {


    @Mapping(target = "id", expression = "java(java.util.UUID.fromString(event.userId()))")
    @Mapping(target = "score", constant = "0L")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "githubUrl", ignore = true)
    @Mapping(target = "linkedinUrl", ignore = true)
    UserProfile toEntity(UserRegisteredEvent event);

    @Mapping(target = "userId", source = "id")
    UserProfileResponse toResponse(UserProfile userProfile);


    void updateEntityFromRequest(UpdateProfileRequest request, @MappingTarget UserProfile entity);
}
