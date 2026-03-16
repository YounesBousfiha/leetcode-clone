package com.leetcode.judgeservice.presentation.controller;


import com.leetcode.judgeservice.application.service.SubmissionService;
import com.leetcode.judgeservice.presentation.dto.SubmissionRequest;
import com.leetcode.judgeservice.presentation.dto.SubmissionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
@Tag(name = "Submissions", description = "Code submission and execution endpoints")
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    @Operation(summary = "Submit code", description = "Submit code for execution against problem test cases")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Code executed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid submission request"),
        @ApiResponse(responseCode = "500", description = "Code execution error")
    })
    public ResponseEntity<SubmissionResponse> submitCode(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String userId,
            @RequestBody SubmissionRequest request
            ) {

        return ResponseEntity.ok(submissionService.submit(UUID.fromString(userId), request));
    }

    @GetMapping("/me")
    @Operation(summary = "Get my submissions", description = "Get paginated list of the authenticated user's submissions")
    @ApiResponse(responseCode = "200", description = "Submissions retrieved successfully")
    public ResponseEntity<Page<SubmissionResponse>> getMySubmissions(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String userId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(submissionService.getUserSubmissions(UUID.fromString(userId), pageable));
    }

    @GetMapping("/me/problem/{problemSlug}")
    @Operation(summary = "Get submissions for problem", description = "Get all user submissions for a specific problem")
    @ApiResponse(responseCode = "200", description = "Submissions retrieved successfully")
    public ResponseEntity<List<SubmissionResponse>> getMySubmissionsForProblem(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "Problem slug") @PathVariable("problemSlug") String problemSlug
    ) {
        return ResponseEntity.ok(submissionService.getUserSubmissionsForProblem(UUID.fromString(userId), problemSlug));
    }

    @GetMapping("/{submissionId}")
    @Operation(summary = "Get submission details", description = "Get details of a specific submission")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Submission retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Submission not found"),
        @ApiResponse(responseCode = "403", description = "Unauthorized access to submission")
    })
    public ResponseEntity<SubmissionResponse> getSubmissionById(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "Submission UUID") @PathVariable("submissionId") UUID submissionId
    ) {
        return ResponseEntity.ok(submissionService.getSubmissionById(submissionId, UUID.fromString(userId)));
    }
}
