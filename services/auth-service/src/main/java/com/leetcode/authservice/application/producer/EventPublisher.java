package com.leetcode.authservice.application.producer;


import com.leetcode.authservice.domain.event.UserRegisteredEvent;
import com.leetcode.authservice.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void sendUserRegisteredEvent(UserRegisteredEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
        log.info("Event sent to RabbitMQ: " + event.email());
    }
}
