package com.leetcode.authservice.infrastructure.seeder;


import com.leetcode.authservice.domain.entity.User;
import com.leetcode.authservice.domain.enums.Role;
import com.leetcode.authservice.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"dev", "local"})
public class AdminSeeder implements CommandLineRunner {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email:admin@codemasters.com}")
    private String email;

    @Value("${admin.password:test123}")
    private String password;



    @Override
    public void run(String... args) {
        String configuredEmail = email == null ? "admin@codemasters.com" : email.trim().toLowerCase();
        String configuredPassword = (password == null || password.isBlank()) ? "Admin@12345" : password;

        User admin = userRepository.findByEmail(configuredEmail)
                .orElseGet(() -> userRepository.findByRole(Role.ADMIN).orElse(null));

        if(admin == null) {
            User rootAdmin = User.builder()
                    .displayName("Root Admin")
                    .email(configuredEmail)
                    .password(passwordEncoder.encode(configuredPassword))
                    .role(Role.ADMIN)
                    .verified(true)
                    .build();

            this.userRepository.save(rootAdmin);
            log.info("Root Admin account seeded with configured credentials");
        } else {
            boolean changed = false;

            if(!admin.getEmail().equalsIgnoreCase(configuredEmail)) {
                boolean emailUsedByAnotherUser = userRepository.findByEmail(configuredEmail)
                        .filter(existing -> !existing.getId().equals(admin.getId()))
                        .isPresent();

                if(emailUsedByAnotherUser) {
                    log.warn("Admin email update skipped because '{}' is already used by another account", configuredEmail);
                } else {
                    admin.setEmail(configuredEmail);
                    changed = true;
                }
            }

            // Force password sync on each startup so configured credentials always work.
            admin.setPassword(passwordEncoder.encode(configuredPassword));
            changed = true;

            if(!admin.isVerified()) {
                admin.setVerified(true);
                changed = true;
            }

            if(admin.getRole() != Role.ADMIN) {
                admin.setRole(Role.ADMIN);
                changed = true;
            }

            if(!"Root Admin".equals(admin.getDisplayName())) {
                admin.setDisplayName("Root Admin");
                changed = true;
            }

            if(changed) {
                userRepository.save(admin);
                log.info("Root Admin account updated with configured credentials");
            } else {
                log.info("Root Admin already up to date");
            }
        }

        seedTestUsers();
    }

    private void seedTestUsers() {
        List<User> testUsers = new ArrayList<>();

        String[][] users = {
                {"john.doe@example.com", "John Doe"},
                {"jane.smith@example.com", "Jane Smith"},
                {"alice.wonder@example.com", "Alice Wonder"},
                {"bob.builder@example.com", "Bob Builder"},
                {"charlie.brown@example.com", "Charlie Brown"}
        };

        for(String[] userData : users) {
            String userEmail = userData[0];
            String displayName = userData[1];

            if(!userRepository.existsByEmail(userEmail)) {
                User testUser = User.builder()
                        .email(userEmail)
                        .displayName(displayName)
                        .password(passwordEncoder.encode("password123"))
                        .role(Role.USER)
                        .verified(true)
                        .build();
                testUsers.add(testUser);
            }
        }

        if(!testUsers.isEmpty()) {
            userRepository.saveAll(testUsers);
            log.info("Seeded {} test users", testUsers.size());
        } else {
            log.info("Test users already exist");
        }
    }
}
