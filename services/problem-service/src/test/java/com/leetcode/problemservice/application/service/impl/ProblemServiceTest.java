package com.leetcode.problemservice.application.service.impl;

import com.leetcode.problemservice.application.mapper.ProblemMapper;
import com.leetcode.problemservice.domain.entity.Problem;
import com.leetcode.problemservice.domain.entity.Tag;
import com.leetcode.problemservice.domain.enums.Difficulty;
import com.leetcode.problemservice.infrastcture.repository.ProblemRepository;
import com.leetcode.problemservice.infrastcture.repository.TagRepository;
import com.leetcode.problemservice.prensentation.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProblemServiceTest {

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private ProblemMapper problemMapper;

    @InjectMocks
    private ProblemService problemService;

    @Test
    @DisplayName("createProblem - should create and return problem")
    void createProblem_shouldCreateAndReturn() {
        CreateProblemRequest request = new CreateProblemRequest(
                "Two Sum", "Given an array...", Difficulty.EASY, 2.0, 256,
                List.of("array", "hash-table"),
                List.of(new TestCaseDto("[2,7,11]", "9", true)),
                List.of(new CodeTemplateDto("java", "class Solution {}")),
                List.of("Use a hash map")
        );

        Problem problem = Problem.builder()
                .id(UUID.randomUUID()).title("Two Sum").slug("two-sum")
                .description("Given an array...").difficulty(Difficulty.EASY).build();
        ProblemDetailResponse expectedResponse = new ProblemDetailResponse(
                problem.getId().toString(), "Two Sum", "two-sum", "Given an array...",
                Difficulty.EASY, 2.0, 256, Set.of(), List.of(), List.of(), List.of("Use a hash map"));

        when(problemRepository.existsBySlug("two-sum")).thenReturn(false);
        when(problemMapper.toEntity(request)).thenReturn(problem);
        when(tagRepository.findByName("array")).thenReturn(Optional.of(Tag.builder().name("array").slug("array").build()));
        when(tagRepository.findByName("hash-table")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(inv -> inv.getArgument(0));
        when(problemRepository.save(any(Problem.class))).thenReturn(problem);
        when(problemMapper.toDetailResponse(problem)).thenReturn(expectedResponse);

        ProblemDetailResponse result = problemService.createProblem(request);

        assertThat(result.title()).isEqualTo("Two Sum");
        assertThat(result.slug()).isEqualTo("two-sum");
        verify(problemRepository).save(any(Problem.class));
    }

    @Test
    @DisplayName("createProblem - should throw when slug already exists")
    void createProblem_shouldThrowWhenDuplicate() {
        CreateProblemRequest request = new CreateProblemRequest(
                "Two Sum", "desc", Difficulty.EASY, 2.0, 256,
                List.of(), List.of(), List.of(), List.of());

        when(problemRepository.existsBySlug("two-sum")).thenReturn(true);

        assertThatThrownBy(() -> problemService.createProblem(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Problem with this title already exists");
    }

    @Test
    @DisplayName("getAllProblems - should return paginated list")
    void getAllProblems_shouldReturnPaginatedList() {
        Pageable pageable = PageRequest.of(0, 20);
        Problem problem = Problem.builder().id(UUID.randomUUID()).title("Test").slug("test").difficulty(Difficulty.EASY).build();
        ProblemListResponse listResponse = new ProblemListResponse(problem.getId().toString(), "Test", "test", Difficulty.EASY, Set.of());
        Page<Problem> page = new PageImpl<>(List.of(problem), pageable, 1);

        when(problemRepository.findAll(pageable)).thenReturn(page);
        when(problemMapper.toListResponse(problem)).thenReturn(listResponse);

        Page<ProblemListResponse> result = problemService.getAllProblems(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("Test");
    }

    @Test
    @DisplayName("getProblemBySlug - should return problem detail")
    void getProblemBySlug_shouldReturnDetail() {
        Problem problem = Problem.builder().id(UUID.randomUUID()).title("Two Sum").slug("two-sum").build();
        ProblemDetailResponse response = new ProblemDetailResponse(
                problem.getId().toString(), "Two Sum", "two-sum", "desc",
                Difficulty.EASY, 2.0, 256, Set.of(), List.of(), List.of(), List.of());

        when(problemRepository.findBySlug("two-sum")).thenReturn(Optional.of(problem));
        when(problemMapper.toDetailResponse(problem)).thenReturn(response);

        ProblemDetailResponse result = problemService.getProblemBySlug("two-sum");

        assertThat(result.slug()).isEqualTo("two-sum");
    }

    @Test
    @DisplayName("getProblemBySlug - should throw when not found")
    void getProblemBySlug_shouldThrowWhenNotFound() {
        when(problemRepository.findBySlug("non-existent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> problemService.getProblemBySlug("non-existent"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Problem not Found");
    }

    @Test
    @DisplayName("deleteProblem - should delete existing problem")
    void deleteProblem_shouldDeleteExisting() {
        UUID id = UUID.randomUUID();
        when(problemRepository.existsById(id)).thenReturn(true);

        problemService.deleteProblem(id.toString());

        verify(problemRepository).deleteById(id);
    }

    @Test
    @DisplayName("deleteProblem - should throw when problem not found")
    void deleteProblem_shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(problemRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> problemService.deleteProblem(id.toString()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Problem not found");
    }

    @Test
    @DisplayName("searchProblems - should search by keyword")
    void searchProblems_shouldSearchByKeyword() {
        Pageable pageable = PageRequest.of(0, 20);
        Problem problem = Problem.builder().id(UUID.randomUUID()).title("Two Sum").slug("two-sum").build();
        ProblemListResponse listResponse = new ProblemListResponse(problem.getId().toString(), "Two Sum", "two-sum", Difficulty.EASY, Set.of());
        Page<Problem> page = new PageImpl<>(List.of(problem), pageable, 1);

        when(problemRepository.searchByKeyword("sum", pageable)).thenReturn(page);
        when(problemMapper.toListResponse(problem)).thenReturn(listResponse);

        Page<ProblemListResponse> result = problemService.searchProblems("sum", pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(problemRepository).searchByKeyword("sum", pageable);
    }

    @Test
    @DisplayName("filterProblems - should filter by difficulty and tags")
    void filterProblems_shouldFilterByDifficultyAndTags() {
        Pageable pageable = PageRequest.of(0, 20);
        List<String> tags = List.of("array");
        Problem problem = Problem.builder().id(UUID.randomUUID()).title("Test").slug("test").difficulty(Difficulty.MEDIUM).build();
        ProblemListResponse listResponse = new ProblemListResponse(problem.getId().toString(), "Test", "test", Difficulty.MEDIUM, Set.of());
        Page<Problem> page = new PageImpl<>(List.of(problem), pageable, 1);

        when(problemRepository.findByDifficultyAndTagSlugs(Difficulty.MEDIUM, tags, pageable)).thenReturn(page);
        when(problemMapper.toListResponse(problem)).thenReturn(listResponse);

        Page<ProblemListResponse> result = problemService.filterProblems(Difficulty.MEDIUM, tags, pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("filterProblems - should filter by difficulty only")
    void filterProblems_shouldFilterByDifficultyOnly() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Problem> page = new PageImpl<>(List.of(), pageable, 0);

        when(problemRepository.findByDifficulty(Difficulty.HARD, pageable)).thenReturn(page);

        Page<ProblemListResponse> result = problemService.filterProblems(Difficulty.HARD, null, pageable);

        assertThat(result.getContent()).isEmpty();
        verify(problemRepository).findByDifficulty(Difficulty.HARD, pageable);
    }

    @Test
    @DisplayName("filterProblems - should return all when no filters")
    void filterProblems_shouldReturnAllWhenNoFilters() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Problem> page = new PageImpl<>(List.of(), pageable, 0);

        when(problemRepository.findAll(pageable)).thenReturn(page);

        Page<ProblemListResponse> result = problemService.filterProblems(null, null, pageable);

        verify(problemRepository).findAll(pageable);
    }

    @Test
    @DisplayName("getProblemForJudge - should return problem for internal use")
    void getProblemForJudge_shouldReturnProblem() {
        Problem problem = Problem.builder().id(UUID.randomUUID()).title("Two Sum").slug("two-sum").build();
        InternalProblemResponse response = new InternalProblemResponse(
                problem.getId().toString(), "two-sum", 2.0, 256, List.of());

        when(problemRepository.findBySlugWithTestCases("two-sum")).thenReturn(Optional.of(problem));
        when(problemMapper.toInternalResponse(problem)).thenReturn(response);

        InternalProblemResponse result = problemService.getProblemForJudge("two-sum");

        assertThat(result.slug()).isEqualTo("two-sum");
        verify(problemMapper).toInternalResponse(problem);
    }
}

