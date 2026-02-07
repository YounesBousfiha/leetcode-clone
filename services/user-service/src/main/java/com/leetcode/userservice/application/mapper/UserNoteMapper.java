package com.leetcode.userservice.application.mapper;


import com.leetcode.userservice.domain.entity.UserNote;
import com.leetcode.userservice.prensetation.dto.UserNoteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserNoteMapper {




    UserNoteResponse toResponse(UserNote note);
}
