package com.leetcode.judgeservice.infrastructure.client.feign;

import com.leetcode.judgeservice.infrastructure.client.dto.UpdateScoreRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserFeignClient {

    @PostMapping("/api/leaderboard/internal/update-score")
    void updateScore(@RequestBody UpdateScoreRequest request);
}

