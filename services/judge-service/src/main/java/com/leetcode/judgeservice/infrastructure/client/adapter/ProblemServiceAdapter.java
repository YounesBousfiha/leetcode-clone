package com.leetcode.judgeservice.infrastructure.client.adapter;

import com.leetcode.judgeservice.application.ports.output.ProblemServicePort;
import com.leetcode.judgeservice.infrastructure.client.dto.ProblemDetailResponse;
import com.leetcode.judgeservice.infrastructure.client.feign.ProblemFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProblemServiceAdapter  implements ProblemServicePort {

    private final ProblemFeignClient feignClient;

    @Override
    public ProblemDetailResponse getProblem(String slug) {
        return feignClient.getProblem(slug);
    }
}
