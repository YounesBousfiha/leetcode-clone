package com.leetcode.problemservice.application.service;

import com.leetcode.problemservice.prensentation.dto.CreateProblemRequest;
import com.leetcode.problemservice.prensentation.dto.ProblemDetailResponse;
import com.leetcode.problemservice.prensentation.dto.ProblemListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProblemService {
    ProblemDetailResponse createProblem(CreateProblemRequest request);

    Page<ProblemListResponse> getAllProblems(Pageable pageable);

    ProblemDetailResponse getProblemBySlug(String slug);

    void deleteProblem(String id);

    ProblemDetailResponse getProblemForJudge(String slug);

}
