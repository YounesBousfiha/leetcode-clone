package com.leetcode.authservice.application.service;

import com.leetcode.authservice.application.producer.EventPublisher;
import com.leetcode.authservice.application.service.impl.AuthService;
import com.leetcode.authservice.domain.entity.User;
import com.leetcode.authservice.domain.enums.Role;
import com.leetcode.authservice.domain.exception.InvalidCredentials;
import com.leetcode.authservice.domain.exception.SameEmailRegistration;
import com.leetcode.authservice.domain.exception.UnverifiedUser;
import com.leetcode.authservice.infrastructure.repository.EmailVerificationRepository;
import com.leetcode.authservice.infrastructure.repository.PasswordResetTokenRepository;
import com.leetcode.authservice.infrastructure.repository.UserRepository;
import com.leetcode.authservice.infrastructure.security.JwtUtil;
import com.leetcode.authservice.infrastructure.service.RedisService;
import com.leetcode.authservice.presentation.dto.request.LoginRequest;
import com.leetcode.authservice.presentation.dto.request.RegisterRequest;
import com.leetcode.authservice.presentation.dto.response.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private RedisService redisService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailVerificationRepository emailVerificationRepository;

    @Mock
    private PasswordResetTokenRepository resetTokenRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .displayName("Test User")
                .password("$2a$10$encodedPassword")
                .role(Role.USER)
                .verified(true)
                .build();

        registerRequest = new RegisterRequest(
                "test@example.com",
                "Test User",
                "password123"
        );

        loginRequest = new LoginRequest(
                "test@example.com",
                "password123"
        );
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(emailVerificationRepository.save(any())).thenReturn(null);
        doNothing().when(eventPublisher).sendUserRegisteredEvent(any());

        // When
        String result = authService.register(registerRequest);

        // Then
        assertThat(result).contains("Registration successful");
        verify(userRepository).existsByEmail(registerRequest.email());
        verify(userRepository).save(any(User.class));
        verify(emailVerificationRepository).save(any());
        verify(eventPublisher).sendUserRegisteredEvent(any());
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(SameEmailRegistration.class)
                .hasMessageContaining("Email Already Exists");

        verify(userRepository).existsByEmail(registerRequest.email());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void shouldLoginSuccessfully() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString(), anyString())).thenReturn("jwt-token");
        doNothing().when(redisService).saveRefreshToken(anyString(), anyString());

        // When
        LoginResponse response = authService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.email()).isEqualTo(testUser.getEmail());
        assertThat(response.userId()).isEqualTo(testUser.getId().toString());

        verify(userRepository).findByEmail(loginRequest.email());
        verify(passwordEncoder).matches(loginRequest.password(), testUser.getPassword());
        verify(jwtUtil).generateToken(anyString(), anyString(), anyString());
        verify(redisService).saveRefreshToken(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentials.class)
                .hasMessageContaining("Email/Password Incorrect");

        verify(userRepository).findByEmail(loginRequest.email());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when password incorrect")
    void shouldThrowExceptionWhenPasswordIncorrect() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentials.class)
                .hasMessageContaining("Email/Password Incorrect");

        verify(userRepository).findByEmail(loginRequest.email());
        verify(passwordEncoder).matches(loginRequest.password(), testUser.getPassword());
        verify(jwtUtil, never()).generateToken(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when user not verified")
    void shouldThrowExceptionWhenUserNotVerified() {
        // Given
        testUser.setVerified(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UnverifiedUser.class)
                .hasMessageContaining("not verified");

        verify(userRepository).findByEmail(loginRequest.email());
        verify(passwordEncoder).matches(loginRequest.password(), testUser.getPassword());
        verify(jwtUtil, never()).generateToken(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void shouldRefreshTokenSuccessfully() {
        // Given
        String refreshToken = "valid-refresh-token";
        when(redisService.extractRefreshTokenSubject(anyString())).thenReturn(testUser.getEmail());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(anyString(), anyString(), anyString())).thenReturn("new-jwt-token");

        // When
        LoginResponse response = authService.refreshToken(refreshToken);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("new-jwt-token");
        assertThat(response.refreshToken()).isEqualTo(refreshToken);

        verify(redisService).extractRefreshTokenSubject(refreshToken);
        verify(userRepository).findByEmail(testUser.getEmail());
        verify(jwtUtil).generateToken(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should logout successfully")
    void shouldLogoutSuccessfully() {
        // Given
        String accessToken = "access-token";
        String refreshToken = "refresh-token";
        when(jwtUtil.expirationDate(anyString())).thenReturn(new java.util.Date(System.currentTimeMillis() + 10000));
        doNothing().when(redisService).blackListAccessToken(anyString(), anyLong());
        doNothing().when(redisService).deleteRefreshToken(anyString());

        // When
        authService.logout(accessToken, refreshToken);

        // Then
        verify(jwtUtil).expirationDate(accessToken);
        verify(redisService).blackListAccessToken(eq(accessToken), anyLong());
        verify(redisService).deleteRefreshToken(refreshToken);
    }
}

