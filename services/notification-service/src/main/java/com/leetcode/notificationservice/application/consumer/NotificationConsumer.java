package com.leetcode.notificationservice.application.consumer;

import com.leetcode.notificationservice.application.service.EmailSenderService;
import com.leetcode.notificationservice.domain.event.PasswordResetEvent;
import com.leetcode.notificationservice.domain.event.UserRegisterEvent;
import com.leetcode.notificationservice.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final EmailSenderService emailSenderService;

    @RabbitListener(queues = RabbitMQConfig.REGISTER_QUEUE_NAME)
    public void handleUserRegistered(UserRegisterEvent event) {
        emailSenderService.sendVerificationEmail(event.email(), event.verificationToken());
    }

    @RabbitListener(queues = RabbitMQConfig.RESET_QUEUE_NAME)
    public void handlePasswordReset(PasswordResetEvent event) {
        emailSenderService.sendPasswordResetEmail(event.email(), event.token());
    }
}
