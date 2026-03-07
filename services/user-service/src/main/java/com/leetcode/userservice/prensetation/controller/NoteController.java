package com.leetcode.userservice.prensetation.controller;


import com.leetcode.userservice.application.service.IUserNoteService;
import com.leetcode.userservice.prensetation.dto.UserNoteRequest;
import com.leetcode.userservice.prensetation.dto.UserNoteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@Tag(name = "User Notes", description = "Personal notes management per problem")
public class NoteController {

    private final IUserNoteService userNoteService;

    @PostMapping
    @Operation(summary = "Save note", description = "Save or update a personal note for a problem")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Note saved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserNoteResponse> saveNote(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String userId,
            @RequestBody UserNoteRequest request
            ) {
        return  ResponseEntity.ok(userNoteService.saveNote(userId, request));
    }


    @GetMapping("/{problemId}")
    @Operation(summary = "Get note", description = "Get the user's note for a specific problem")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Note retrieved successfully")
    })
    public ResponseEntity<UserNoteResponse> getNote(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "Problem ID") @PathVariable String problemId

    ) {
        return ResponseEntity.ok(userNoteService.getNote(userId, problemId));
    }

}
