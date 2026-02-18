package com.leetcode.judgeservice.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CodeExecutionException extends JudgeServiceException {
    public CodeExecutionException(String message) {
        super("Execution failed: " + message);
    }
}
