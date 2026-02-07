package com.leetcode.userservice.infrastructure.repository;

import com.leetcode.userservice.domain.entity.UserNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserNoteRepository extends JpaRepository<UserNote, String> {
    UserNote findByUserProfileIdAndProblemId(UUID userId, String problemId);
}
