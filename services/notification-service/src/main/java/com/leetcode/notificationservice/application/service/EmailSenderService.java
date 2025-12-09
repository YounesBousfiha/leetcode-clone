package com.leetcode.notificationservice.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String verificationToken) {
        log.info("=============== EMAIL SENDING ===============");
        log.info("To: {}", toEmail);
        log.info("Token: {}", verificationToken);
        log.info("Link: http://localhost:8080/auth-service/api/auth/verify?token={}", verificationToken);
        log.info("=============================================");

        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(toEmail);
            email.setSubject("Welcome to CodeMasters");
            email.setText("Click here to verify: http://localhost:8080/auth-service/api/auth/verify?token=" + verificationToken);

            mailSender.send(email);
        } catch (Exception e) {
            log.error("Failed to send Email: {}", e.getMessage());
        }
    }
}
