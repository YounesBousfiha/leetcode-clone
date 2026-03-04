package com.leetcode.aiassistant.application.service;

import com.leetcode.aiassistant.presentation.dto.AIHintResponse;
import com.leetcode.aiassistant.presentation.dto.CodeReviewResponse;

public interface IAIAssistService {
    AIHintResponse getHint(String problemSlug, String userCode, String language);
    AIHintResponse explainProblem(String problemSlug);
    CodeReviewResponse reviewCode(String code, String language, String problemSlug);
}

