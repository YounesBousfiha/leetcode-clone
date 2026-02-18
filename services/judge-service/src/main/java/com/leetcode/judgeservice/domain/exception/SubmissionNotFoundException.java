package com.leetcode.judgeservice.domain.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SubmissionNotFoundException extends JudgeServiceException {
    public SubmissionNotFoundException(String id) {
        super("Submission not found with ID: " + id);
    }
}
