package com.leetcode.userservice.application.consumer;

import com.leetcode.userservice.application.service.IUserProfileService;
import com.leetcode.userservice.domain.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final IUserProfileService userProfileService;

    @RabbitListener(queues = "user.profile.queue")
    public void handleUserRegistered(Map<String, Object> payload) {
        try {
            String userId = (String) payload.get("userId");
            String email = (String) payload.get("email");
            String displayName = (String) payload.get("displayName");
            String verificationToken = (String) payload.get("verificationToken");

            log.info("Received UserRegisteredEvent for email: {}", email);
            userProfileService.createNewProfile(
                    new UserRegisteredEvent(userId, email, displayName, verificationToken)
            );
        } catch (Exception e) {
            log.error("Error creating user profile: {}", e.getMessage());
            throw new AmqpRejectAndDontRequeueException("Moving to DLQ", e);
        }
    }
}
