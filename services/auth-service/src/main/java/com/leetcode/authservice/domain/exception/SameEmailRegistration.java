package com.leetcode.authservice.domain.exception;

public class SameEmailRegistration extends RuntimeException {
    public SameEmailRegistration(String message) {
        super(message);
    }
}
