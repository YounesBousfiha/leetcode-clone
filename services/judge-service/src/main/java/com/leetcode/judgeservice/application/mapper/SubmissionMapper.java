package com.leetcode.judgeservice.application.mapper;


import com.leetcode.judgeservice.domain.entity.Submission;
import com.leetcode.judgeservice.domain.entity.SubmissionResult;
import com.leetcode.judgeservice.presentation.dto.SubmissionRequest;
import com.leetcode.judgeservice.presentation.dto.SubmissionResponse;
import com.leetcode.judgeservice.presentation.dto.SubmissionResultResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {


    @Mapping(target = "details", source = "resultList")
    SubmissionResponse toResponse(Submission submission);


    SubmissionResultResponse toResultResponse(SubmissionResult result);



    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "userID" ,ignore = true)
    @Mapping(target = "language", expression = "java(com.leetcode.judgeservice.domain.enums.ProgrammingLanguage.JAVA)")
    Submission toEntity(SubmissionRequest request);
}
