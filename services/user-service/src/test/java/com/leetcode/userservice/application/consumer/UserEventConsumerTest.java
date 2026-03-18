package com.leetcode.userservice.application.consumer;

import com.leetcode.userservice.application.service.IUserProfileService;
import com.leetcode.userservice.domain.event.UserRegisteredEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserEventConsumerTest {

    @Mock
    private IUserProfileService userProfileService;

    @InjectMocks
    private UserEventConsumer userEventConsumer;

    @Test
    @DisplayName("handleUserRegistered - should delegate to userProfileService")
    void handleUserRegistered_shouldDelegateToService() {
        Map<String, Object> payload = Map.of(
                "userId", "user-1",
                "email", "test@example.com",
                "displayName", "Test",
                "verificationToken", "token"
        );
        UserRegisteredEvent expectedEvent = new UserRegisteredEvent("user-1", "test@example.com", "Test", "token");

        userEventConsumer.handleUserRegistered(payload);

        verify(userProfileService).createNewProfile(expectedEvent);
    }

    @Test
    @DisplayName("handleUserRegistered - should throw AmqpRejectAndDontRequeueException on error")
    void handleUserRegistered_shouldRejectOnError() {
        Map<String, Object> payload = Map.of(
                "userId", "user-1",
                "email", "test@example.com",
                "displayName", "Test",
                "verificationToken", "token"
        );
        doThrow(new RuntimeException("DB error")).when(userProfileService).createNewProfile(any(UserRegisteredEvent.class));

        assertThatThrownBy(() -> userEventConsumer.handleUserRegistered(payload))
                .isInstanceOf(AmqpRejectAndDontRequeueException.class);
    }
}
