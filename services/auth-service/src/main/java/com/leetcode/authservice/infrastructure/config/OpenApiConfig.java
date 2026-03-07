package com.leetcode.authservice.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Auth Service API",
                version = "1.0.0",
                description = "Authentication and authorization service - JWT login, registration, email verification, password reset",
                contact = @Contact(name = "LeetCode Clone Team")
        ),
        servers = {
                @Server(url = "/", description = "Default Server")
        }
)
public class OpenApiConfig {
}

