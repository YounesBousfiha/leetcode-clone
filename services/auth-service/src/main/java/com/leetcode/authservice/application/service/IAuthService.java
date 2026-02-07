package com.leetcode.authservice.application.service;

import com.leetcode.authservice.presentation.dto.request.ForgetPasswordRequest;
import com.leetcode.authservice.presentation.dto.request.LoginRequest;
import com.leetcode.authservice.presentation.dto.request.RegisterRequest;
import com.leetcode.authservice.presentation.dto.request.ResetPasswordRequest;
import com.leetcode.authservice.presentation.dto.response.LoginResponse;

public interface IAuthService {

    String register(RegisterRequest registerRequest);

    LoginResponse login(LoginRequest loginRequest);

    String verifyAccount(String token);

    String initiatePasswordReset(ForgetPasswordRequest request);

    LoginResponse refreshToken(String refreshToken);

    String resetPassword(ResetPasswordRequest request, String token);

    void logout(String accessToken, String refreshToken);
}
