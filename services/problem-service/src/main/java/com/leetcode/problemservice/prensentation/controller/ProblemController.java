package com.leetcode.problemservice.prensentation.controller;


import com.leetcode.problemservice.application.service.IProblemService;
import com.leetcode.problemservice.domain.enums.Difficulty;
import com.leetcode.problemservice.prensentation.dto.CreateProblemRequest;
import com.leetcode.problemservice.prensentation.dto.InternalProblemResponse;
import com.leetcode.problemservice.prensentation.dto.ProblemDetailResponse;
import com.leetcode.problemservice.prensentation.dto.ProblemListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Problems", description = "Problem management and search endpoints")
public class ProblemController {

    private final IProblemService problemService;

    @PostMapping
    @Operation(summary = "Create problem", description = "Create a new coding problem (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Problem created successfully"),
        @ApiResponse(responseCode = "409", description = "Problem with this title already exists")
    })
    public ResponseEntity<ProblemDetailResponse> createProblem(@RequestBody CreateProblemRequest request) {
        ProblemDetailResponse response = problemService.createProblem(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all problems", description = "Get paginated list of all problems")
    @ApiResponse(responseCode = "200", description = "Problems retrieved successfully")
    public ResponseEntity<Page<ProblemListResponse>> getAllProblems(
            @PageableDefault(size = 20)Pageable pageable
            ) {
        return ResponseEntity.ok(problemService.getAllProblems(pageable));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter problems", description = "Filter problems by difficulty and/or tags")
    @ApiResponse(responseCode = "200", description = "Filtered problems retrieved")
    public ResponseEntity<Page<ProblemListResponse>> filterProblems(
            @Parameter(description = "Difficulty level") @RequestParam(required = false) Difficulty difficulty,
            @Parameter(description = "Tag slugs") @RequestParam(required = false) List<String> tags,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(problemService.filterProblems(difficulty, tags, pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search problems", description = "Full-text search problems by title or description")
    @ApiResponse(responseCode = "200", description = "Search results retrieved")
    public ResponseEntity<Page<ProblemListResponse>> searchProblems(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(problemService.searchProblems(keyword, pageable));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get problem by slug", description = "Get detailed problem information by slug")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Problem retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Problem not found")
    })
    public ResponseEntity<ProblemDetailResponse> getProblemBySlug(
            @Parameter(description = "Problem slug") @PathVariable("slug") String slug
    ) {
        log.info("SLUG: {}", slug);
        return ResponseEntity.ok(problemService.getProblemBySlug(slug));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete problem", description = "Delete a problem by ID (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Problem deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Problem not found")
    })
    public ResponseEntity<Void> deleteProblem(@Parameter(description = "Problem UUID") @PathVariable String id) {
        problemService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/internal/{slug}")
    @Operation(summary = "Get problem for judge (internal)", description = "Internal endpoint used by Judge Service via Feign")
    @ApiResponse(responseCode = "200", description = "Problem retrieved for judging")
    public ResponseEntity<InternalProblemResponse> getProblemForJudge(
            @Parameter(description = "Problem slug") @PathVariable("slug") String slug) {
        return ResponseEntity.ok(problemService.getProblemForJudge(slug));
    }
}
