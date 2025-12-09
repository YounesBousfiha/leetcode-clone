package com.leetcode.authservice.infrastructure.seeder;


import com.leetcode.authservice.domain.entity.User;
import com.leetcode.authservice.domain.enums.Role;
import com.leetcode.authservice.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
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
            log.info("Root Admin Account seeded");
        }
        log.info("Root Admin Already in the Database");
    }
}
