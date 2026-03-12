package com.leetcode.judgeservice.infrastructure.repository;


import com.leetcode.judgeservice.domain.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {

    List<Submission> findByUserIDAndProblemSlug(UUID userId, String problemSlug);

    List<Submission> findByUserID(UUID userId);

    Page<Submission> findByUserID(UUID userId, Pageable pageable);
}
