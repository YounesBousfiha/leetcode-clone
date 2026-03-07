package com.leetcode.problemservice.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Problem Service API",
                version = "1.0.0",
                description = "Coding problem management - CRUD, search, filter by difficulty and tags",
                contact = @Contact(name = "LeetCode Clone Team")
        ),
        servers = {
                @Server(url = "/", description = "Default Server")
        }
)
public class OpenApiConfig {
}

