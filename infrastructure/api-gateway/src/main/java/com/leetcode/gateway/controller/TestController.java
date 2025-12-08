package com.leetcode.gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Value("${custom.message:default}")
    private String message;

    @GetMapping("/message")
    public String getMessage() {
        return message;
    }
}
