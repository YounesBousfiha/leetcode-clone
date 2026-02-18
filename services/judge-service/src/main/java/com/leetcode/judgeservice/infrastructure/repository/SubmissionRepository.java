package com.leetcode.judgeservice.infrastructure.repository;


import com.leetcode.judgeservice.domain.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {

    List<Submission> findByUserIdAndProblemId(UUID userId, String problemId);

    List<Submission> findByUserId(UUID userId);
}
