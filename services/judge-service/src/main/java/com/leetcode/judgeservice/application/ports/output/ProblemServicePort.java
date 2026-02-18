package com.leetcode.judgeservice.application.ports.output;

import com.leetcode.judgeservice.infrastructure.client.dto.ProblemDetailResponse;
import org.springframework.http.ProblemDetail;

public interface ProblemServicePort {
    ProblemDetailResponse getProblem(String slug);
}
