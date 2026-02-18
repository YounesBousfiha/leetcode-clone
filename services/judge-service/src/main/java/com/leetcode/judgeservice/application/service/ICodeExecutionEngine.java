package com.leetcode.judgeservice.application.service;

import com.leetcode.judgeservice.domain.entity.SubmissionResult;

public interface ICodeExecutionEngine {

    SubmissionResult executeCode(String userCode, String language, String input, String expectedOutput);
}
