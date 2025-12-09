package com.leetcode.authservice.presentation.dto.advicer;


import com.leetcode.authservice.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final  String TIMESTAMP = "timestamp";

    @ExceptionHandler(InvalidCredentials.class)
    public ProblemDetail handleInvalidCredentialException(Exception ex ,WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );

        problemDetail.setTitle("Invalid Credentials");
        problemDetail.setDetail("Invalid Credentials");
        problemDetail.setProperty("path", request.getContextPath());
        problemDetail.setProperty(TIMESTAMP, LocalDateTime.now());

        return problemDetail;
    }

    @ExceptionHandler(SameEmailRegistration.class)
    public ProblemDetail handleSameEmailRegistrationException(Exception ex ,WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );

        problemDetail.setTitle("Email Already Exists");
        problemDetail.setDetail("Email Already Exists");
        problemDetail.setProperty("path", request.getContextPath());
        problemDetail.setProperty(TIMESTAMP, LocalDateTime.now());

        return problemDetail;
    }

    @ExceptionHandler(UnverifiedUser.class)
    public ProblemDetail handleUnverifiedUserException(Exception ex ,WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );

        problemDetail.setTitle("User Not Verified");
        problemDetail.setDetail("User Not Verified");
        problemDetail.setProperty("path", request.getContextPath());
        problemDetail.setProperty(TIMESTAMP, LocalDateTime.now());

        return problemDetail;
    }

    @ExceptionHandler(TokenExpire.class)
    public ProblemDetail handleTokenExpireException(Exception ex ,WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.GONE,
                ex.getMessage()
        );

        problemDetail.setTitle("Token Expired");
        problemDetail.setDetail("Token Expired");
        problemDetail.setProperty("path", request.getContextPath());
        problemDetail.setProperty(TIMESTAMP, LocalDateTime.now());

        return problemDetail;
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ProblemDetail handleResourceNotFoundException(Exception ex ,WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );

        problemDetail.setTitle("Resource Not Found");
        problemDetail.setDetail("Resource Not Found");
        problemDetail.setProperty("path", request.getContextPath());
        problemDetail.setProperty(TIMESTAMP, LocalDateTime.now());

        return problemDetail;
    }
}
