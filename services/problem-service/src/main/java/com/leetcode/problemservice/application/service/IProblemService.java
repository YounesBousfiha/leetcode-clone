package com.leetcode.problemservice.application.service;

import com.leetcode.problemservice.domain.enums.Difficulty;
import com.leetcode.problemservice.prensentation.dto.CreateProblemRequest;
import com.leetcode.problemservice.prensentation.dto.ProblemDetailResponse;
import com.leetcode.problemservice.prensentation.dto.ProblemListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProblemService {
    ProblemDetailResponse createProblem(CreateProblemRequest request);

    Page<ProblemListResponse> getAllProblems(Pageable pageable);

    ProblemDetailResponse getProblemBySlug(String slug);

    void deleteProblem(String id);

    ProblemDetailResponse getProblemForJudge(String slug);

    Page<ProblemListResponse> getProblemsByDifficulty(Difficulty difficulty, Pageable pageable);

    Page<ProblemListResponse> getProblemsByTags(List<String> tagSlugs, Pageable pageable);

    Page<ProblemListResponse> searchProblems(String keyword, Pageable pageable);

    Page<ProblemListResponse> filterProblems(Difficulty difficulty, List<String> tagSlugs, Pageable pageable);
}
