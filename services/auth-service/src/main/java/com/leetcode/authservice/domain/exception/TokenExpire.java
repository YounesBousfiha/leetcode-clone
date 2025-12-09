package com.leetcode.authservice.domain.exception;

public class TokenExpire extends RuntimeException {
    public TokenExpire(String message) {
        super(message);
    }
}
