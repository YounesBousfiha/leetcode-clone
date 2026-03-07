package com.leetcode.authservice.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leetcode.authservice.application.service.IAuthService;
import com.leetcode.authservice.presentation.dto.request.*;
import com.leetcode.authservice.presentation.dto.response.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IAuthService authService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    @DisplayName("POST /api/auth/login - should return 200 with LoginResponse")
    void login_shouldReturnLoginResponse() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        LoginResponse response = LoginResponse.builder()
                .accessToken("jwt-token")
                .refreshToken("refresh-token")
                .email("test@example.com")
                .userId("user-id")
                .role("USER")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/register - should return 201 with message")
    void register_shouldReturn201WithMessage() throws Exception {
        RegisterRequest request = new RegisterRequest("test@example.com", "password123", "TestUser");
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn("Registration successful. Please check your email to verify your account.");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Registration successful. Please check your email to verify your account."));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("GET /api/auth/verify - should return 200 with success message")
    void verify_shouldReturnSuccessMessage() throws Exception {
        when(authService.verifyAccount("valid-token"))
                .thenReturn("Account verified successfully. now You can Login");

        mockMvc.perform(get("/api/auth/verify")
                        .param("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("Account verified successfully. now You can Login"));

        verify(authService).verifyAccount("valid-token");
    }

    @Test
    @DisplayName("POST /api/auth/forget-password - should return 200")
    void forgotPassword_shouldReturnSuccessMessage() throws Exception {
        ForgetPasswordRequest request = new ForgetPasswordRequest("test@example.com");
        when(authService.initiatePasswordReset(any(ForgetPasswordRequest.class)))
                .thenReturn("Password reset link sent to your email");

        mockMvc.perform(post("/api/auth/forget-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset link sent to your email"));

        verify(authService).initiatePasswordReset(any(ForgetPasswordRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/reset-password - should return 200")
    void resetPassword_shouldReturnSuccessMessage() throws Exception {
        ResetPasswordRequest request = ResetPasswordRequest.builder().newPassword("newPassword123").build();
        when(authService.resetPassword(any(ResetPasswordRequest.class), eq("reset-token")))
                .thenReturn("Password reset successfully. you can now Login");

        mockMvc.perform(post("/api/auth/reset-password")
                        .param("token", "reset-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset successfully. you can now Login"));

        verify(authService).resetPassword(any(ResetPasswordRequest.class), eq("reset-token"));
    }

    @Test
    @DisplayName("POST /api/auth/refresh-token - should return 200 with new tokens")
    void refreshToken_shouldReturnNewTokens() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("old-refresh-token");
        LoginResponse response = LoginResponse.builder()
                .accessToken("new-jwt-token")
                .refreshToken("old-refresh-token")
                .email("test@example.com")
                .userId("user-id")
                .role("USER")
                .build();

        when(authService.refreshToken("old-refresh-token")).thenReturn(response);

        mockMvc.perform(post("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("old-refresh-token"));

        verify(authService).refreshToken("old-refresh-token");
    }

    @Test
    @DisplayName("POST /api/auth/logout - should return 204 No Content")
    void logout_shouldReturn204() throws Exception {
        LogoutRequest request = new LogoutRequest("refresh-token");
        doNothing().when(authService).logout(anyString(), anyString());

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer jwt-access-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(authService).logout(eq("jwt-access-token"), eq("refresh-token"));
    }

    @Test
    @DisplayName("POST /api/auth/logout - should handle Authorization header without Bearer prefix")
    void logout_shouldHandleAuthHeaderWithoutBearer() throws Exception {
        LogoutRequest request = new LogoutRequest("refresh-token");
        doNothing().when(authService).logout(anyString(), anyString());

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "raw-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(authService).logout(eq("raw-token"), eq("refresh-token"));
    }
}

