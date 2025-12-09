package com.leetcode.authservice.infrastructure.repository;


import com.leetcode.authservice.domain.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
