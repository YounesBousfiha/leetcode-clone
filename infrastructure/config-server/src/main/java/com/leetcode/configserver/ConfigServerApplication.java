package com.leetcode.configserver;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.config.server.environment.ConfigTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }

    @Bean
    public ConfigTokenProvider configTokenProvider() {
        return new ConfigTokenProvider() {
            @Override
            public String getToken() {
                String token = System.getenv("VAULT_TOKEN");

                if (!StringUtils.hasText(token)) {
                    throw new IllegalStateException("❌ Error: VAULT_TOKEN not found in environment variables!");
                }

                return token;
            }
        };
    }
}
