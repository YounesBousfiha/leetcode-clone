package com.leetcode.userservice.prensetation.controller;


import com.leetcode.userservice.application.service.IUserNoteService;
import com.leetcode.userservice.prensetation.dto.UserNoteRequest;
import com.leetcode.userservice.prensetation.dto.UserNoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final IUserNoteService userNoteService;

    @PostMapping
    public ResponseEntity<UserNoteResponse> saveNote(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody UserNoteRequest request
            ) {
        return  ResponseEntity.ok(userNoteService.saveNote(userId, request));
    }


    @GetMapping("/{problemId}")
    public ResponseEntity<UserNoteResponse> getNote(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String problemId

    ) {
        return ResponseEntity.ok(userNoteService.getNote(userId, problemId));
    }

}
