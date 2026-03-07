package com.leetcode.authservice.application.producer;

import com.leetcode.authservice.domain.event.PasswordResetEvent;
import com.leetcode.authservice.domain.event.UserRegisteredEvent;
import com.leetcode.authservice.infrastructure.config.RabbitMQConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private EventPublisher eventPublisher;

    @Test
    @DisplayName("sendUserRegisteredEvent - should publish event to exchange with routing key")
    void sendUserRegisteredEvent_shouldPublishEvent() {
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .userId("user-123")
                .email("test@example.com")
                .displayName("Test User")
                .verificationToken("token-abc")
                .build();

        eventPublisher.sendUserRegisteredEvent(event);

        verify(rabbitTemplate).convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
    }

    @Test
    @DisplayName("sendPasswordResetEvent - should publish event to exchange with reset routing key")
    void sendPasswordResetEvent_shouldPublishEvent() {
        PasswordResetEvent event = PasswordResetEvent.builder()
                .email("test@example.com")
                .token("reset-token-xyz")
                .displayName("Test User")
                .build();

        eventPublisher.sendPasswordResetEvent(event);

        verify(rabbitTemplate).convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.RESET_ROUTING_KEY,
                event
        );
    }
}

