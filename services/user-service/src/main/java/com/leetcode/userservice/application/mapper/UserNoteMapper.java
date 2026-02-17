package com.leetcode.userservice.application.mapper;


import com.leetcode.userservice.domain.entity.UserNote;
import com.leetcode.userservice.prensetation.dto.UserNoteRequest;
import com.leetcode.userservice.prensetation.dto.UserNoteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserNoteMapper {




    UserNoteResponse toResponse(UserNote note);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    @Mapping(target = "lastUpdated", ignore = true)
    void updateEntity(UserNoteRequest request,  @MappingTarget UserNote userNote);
}


