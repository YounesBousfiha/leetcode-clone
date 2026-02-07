package com.leetcode.userservice.application.consumer;

import com.leetcode.userservice.application.service.IUserProfileService;
import com.leetcode.userservice.domain.entity.UserProfile;
import com.leetcode.userservice.domain.event.UserRegisteredEvent;
import com.leetcode.userservice.infrastructure.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final IUserProfileService userProfileService;

    @RabbitListener(queues = "user.profile.queue")
    public void handleUserRegistered(UserRegisteredEvent event) {
        try {
            log.info("Received UserRegisteredEvent for email: {}", event.email());
            userProfileService.createNewProfile(event);
        } catch (Exception e) {
            log.error("Error creating user profile: {}", e.getMessage());

            throw new AmqpRejectAndDontRequeueException("Moving to DLQ", e);
        }
    }
}
