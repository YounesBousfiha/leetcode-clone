package com.leetcode.judgeservice.infrastructure.execution;

import com.leetcode.judgeservice.application.service.ICodeExecutionEngine;
import com.leetcode.judgeservice.domain.entity.SubmissionResult;

public class KubernetesExecutionEngine  implements ICodeExecutionEngine {
    @Override
    public SubmissionResult executeCode(String userCode, String language, String input, String expectedOutput) {
        return null;
    }
}
