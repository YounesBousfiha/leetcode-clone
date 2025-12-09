package com.leetcode.userservice.application.consumer;

import com.leetcode.userservice.domain.entity.UserProfile;
import com.leetcode.userservice.domain.event.UserRegisteredEvent;
import com.leetcode.userservice.infrastructure.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserEventConsumer {

    private final UserProfileRepository userProfileRepository;

    @RabbitListener(queues = "user.profile.queue")
    public void handleUserRegistered(UserRegisteredEvent event) {

        UserProfile profile = UserProfile.builder()
                .id(UUID.fromString(event.userId()))
                .email(event.email())
                .displayName(event.displayName())
                .score(0L)
                .build();
        userProfileRepository.save(profile);
    }
}
