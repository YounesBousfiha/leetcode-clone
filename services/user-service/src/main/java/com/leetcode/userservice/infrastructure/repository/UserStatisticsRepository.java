package com.leetcode.userservice.infrastructure.repository;

import com.leetcode.userservice.domain.entity.UserStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserStatisticsRepository extends JpaRepository<UserStatistics, UUID> {
}

