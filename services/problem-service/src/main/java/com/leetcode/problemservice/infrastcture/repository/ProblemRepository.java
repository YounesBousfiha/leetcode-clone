package com.leetcode.problemservice.infrastcture.repository;

import com.leetcode.problemservice.domain.entity.Problem;
import com.leetcode.problemservice.domain.enums.Difficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, UUID> {

    Optional<Problem> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<Problem> findByDifficulty(Difficulty difficulty, Pageable pageable);

    @Query("SELECT p FROM Problem p JOIN p.tags t WHERE t.slug IN :tagSlugs")
    Page<Problem> findByTagSlugs(@Param("tagSlugs") List<String> tagSlugs, Pageable pageable);

    @Query("SELECT p FROM Problem p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Problem> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Problem p JOIN p.tags t WHERE p.difficulty = :difficulty AND t.slug IN :tagSlugs")
    Page<Problem> findByDifficultyAndTagSlugs(
            @Param("difficulty") Difficulty difficulty,
            @Param("tagSlugs") List<String> tagSlugs,
            Pageable pageable
    );
}
