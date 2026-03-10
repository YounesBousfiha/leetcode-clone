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
    public void run(String... args) throws Exception {

        boolean isAdminExist = userRepository.existsByEmail(email);

        if(!isAdminExist) {
            User rootAdmin = User.builder()
                    .displayName("Root Admin")
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(Role.ADMIN)
                    .verified(true)
                    .build();


            this.userRepository.save(rootAdmin);
            log.info("✅ Root Admin Account seeded");
        } else {
            log.info("✅ Root Admin Already in the Database");
        }

        // Seed test users
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

        for (String[] userData : users) {
            String userEmail = userData[0];
            String displayName = userData[1];

            if (!userRepository.existsByEmail(userEmail)) {
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

        if (!testUsers.isEmpty()) {
            userRepository.saveAll(testUsers);
            log.info("✅ Seeded {} test users", testUsers.size());
        } else {
            log.info("✅ Test users already exist");
        }
    }
}
