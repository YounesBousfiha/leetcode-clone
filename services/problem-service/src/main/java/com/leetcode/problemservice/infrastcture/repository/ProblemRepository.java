package com.leetcode.problemservice.infrastcture.repository;

import com.leetcode.problemservice.domain.entity.Problem;
import com.leetcode.problemservice.domain.enums.Difficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, UUID> {

    Optional<Problem> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<Problem> findByDifficulty(Difficulty difficulty, Pageable pageable);
}
