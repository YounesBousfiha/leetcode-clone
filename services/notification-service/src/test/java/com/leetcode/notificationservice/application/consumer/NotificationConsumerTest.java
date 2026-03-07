package com.leetcode.notificationservice.application.consumer;

import com.leetcode.notificationservice.application.service.EmailSenderService;
import com.leetcode.notificationservice.domain.event.PasswordResetEvent;
import com.leetcode.notificationservice.domain.event.UserRegisterEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private NotificationConsumer notificationConsumer;

    @Test
    @DisplayName("handleUserRegistered - should call sendVerificationEmail")
    void handleUserRegistered_shouldSendVerificationEmail() {
        UserRegisterEvent event = UserRegisterEvent.builder()
                .userId("user-1")
                .email("test@example.com")
                .displayName("Test User")
                .verificationToken("verify-token")
                .build();

        notificationConsumer.handleUserRegistered(event);

        verify(emailSenderService).sendVerificationEmail("test@example.com", "verify-token");
    }

    @Test
    @DisplayName("handlePasswordReset - should call sendPasswordResetEmail")
    void handlePasswordReset_shouldSendPasswordResetEmail() {
        PasswordResetEvent event = new PasswordResetEvent("test@example.com", "reset-token", "Test User");

        notificationConsumer.handlePasswordReset(event);

        verify(emailSenderService).sendPasswordResetEmail("test@example.com", "reset-token");
    }
}

