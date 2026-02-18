package com.leetcode.judgeservice.infrastructure.client.feign;


import com.leetcode.judgeservice.infrastructure.client.dto.ProblemDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "problem-service", path = "/api/problems")
public interface ProblemFeignClient {

    @GetMapping("/{slug}")
    ProblemDetailResponse getProblem(@PathVariable("slug") String slug);
}
