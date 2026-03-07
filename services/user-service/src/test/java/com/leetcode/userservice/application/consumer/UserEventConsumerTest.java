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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserEventConsumerTest {

    @Mock
    private IUserProfileService userProfileService;

    @InjectMocks
    private UserEventConsumer userEventConsumer;

    @Test
    @DisplayName("handleUserRegistered - should delegate to userProfileService")
    void handleUserRegistered_shouldDelegateToService() {
        UserRegisteredEvent event = new UserRegisteredEvent("user-1", "test@example.com", "Test", "token");

        userEventConsumer.handleUserRegistered(event);

        verify(userProfileService).createNewProfile(event);
    }

    @Test
    @DisplayName("handleUserRegistered - should throw AmqpRejectAndDontRequeueException on error")
    void handleUserRegistered_shouldRejectOnError() {
        UserRegisteredEvent event = new UserRegisteredEvent("user-1", "test@example.com", "Test", "token");
        doThrow(new RuntimeException("DB error")).when(userProfileService).createNewProfile(event);

        assertThatThrownBy(() -> userEventConsumer.handleUserRegistered(event))
                .isInstanceOf(AmqpRejectAndDontRequeueException.class);
    }
}

