package com.leetcode.authservice.infrastructure.repository;


import com.leetcode.authservice.domain.entity.User;
import com.leetcode.authservice.domain.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByRole(Role role);
}
