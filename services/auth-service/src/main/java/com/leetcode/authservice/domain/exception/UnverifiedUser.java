package com.leetcode.authservice.domain.exception;

public class UnverifiedUser extends RuntimeException {
    public UnverifiedUser(String message) {
        super(message);
    }
}
