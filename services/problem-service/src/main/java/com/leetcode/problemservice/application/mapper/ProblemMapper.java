package com.leetcode.problemservice.application.mapper;


import com.leetcode.problemservice.domain.entity.CodeTemplate;
import com.leetcode.problemservice.domain.entity.Problem;
import com.leetcode.problemservice.domain.entity.Tag;
import com.leetcode.problemservice.domain.entity.TestCase;
import com.leetcode.problemservice.prensentation.dto.*;
import java.util.Collections;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProblemMapper {

    @Mapping(target = "examples", source = "testCases", qualifiedByName = "filterPublicTestCases")
    @Mapping(target = "templates", source= "codeTemplates")
    ProblemDetailResponse toDetailResponse(Problem problem);

    ProblemListResponse toListResponse(Problem problem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "testCases", ignore = true)
    @Mapping(target = "codeTemplates", ignore = true)
    Problem toEntity(CreateProblemRequest request);


    @Named("filterPublicTestCases")
    default List<TestCaseDto> filterPublicTestCases(List<TestCase> testCases) {
        if(testCases == null) return Collections.emptyList();
        return testCases.stream()
                .filter(TestCase::isPublic)
                .map(this::toTestCaseDto)
                .toList();
    }

    TestCaseDto toTestCaseDto(TestCase testCase);

    CodeTemplateDto toTemplateDto(CodeTemplate codeTemplate);

    TestCase toTestCaseEntity(TestCaseDto dto);

    CodeTemplate toCodeTemplateEntity(CodeTemplateDto dto);

    TagDto toTagDto(Tag tag);


    @Mapping(target = "examples", source = "testCases")
    @Mapping(target = "templates", source = "codeTemplates")
    ProblemDetailResponse toInternalResponse(Problem problem);

}
