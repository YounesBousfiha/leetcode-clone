package com.leetcode.problemservice.prensentation.controller;


import com.leetcode.problemservice.application.service.IProblemService;
import com.leetcode.problemservice.prensentation.dto.CreateProblemRequest;
import com.leetcode.problemservice.prensentation.dto.ProblemDetailResponse;
import com.leetcode.problemservice.prensentation.dto.ProblemListResponse;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
@Slf4j
public class ProblemController {

    private final IProblemService problemService;

    @PostMapping
    public ResponseEntity<ProblemDetailResponse> createProblem(@RequestBody CreateProblemRequest request) {
        ProblemDetailResponse response = problemService.createProblem(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<ProblemListResponse>> getAllProblems(
            @PageableDefault(size = 20)Pageable pageable
            ) {
        return ResponseEntity.ok(problemService.getAllProblems(pageable));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ProblemDetailResponse> getProblemBySlug(
            @PathVariable("slug") String slug
    ) {
        log.info("SLUG: {}", slug);
        return ResponseEntity.ok(problemService.getProblemBySlug(slug));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable String id) {
        problemService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }
}
