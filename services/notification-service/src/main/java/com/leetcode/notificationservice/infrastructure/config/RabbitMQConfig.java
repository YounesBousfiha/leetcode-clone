package com.leetcode.notificationservice.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "auth.exchange";
    public static final String REGISTER_ROUTING_KEY = "auth.user.registered";
    public static final String RESET_ROUTING_KEY = "auth.password.reset";

    public static final String REGISTER_QUEUE_NAME = "notification.email.register.queue";
    public static final String RESET_QUEUE_NAME = "notification.email.reset.queue";

    @Bean
    public Queue registerQueue() {
        return new Queue(REGISTER_QUEUE_NAME, true);
    }

    @Bean
    public Queue resetQueue() {
        return new Queue(RESET_QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding registerBinding(@Qualifier("registerQueue") Queue registerQueue, TopicExchange exchange) {
        return BindingBuilder.bind(registerQueue).to(exchange).with(REGISTER_ROUTING_KEY);
    }


    @Bean
    public Binding resetBinding(@Qualifier("resetQueue") Queue resetQueue, TopicExchange exchange) {
        return BindingBuilder.bind(resetQueue).to(exchange).with(RESET_ROUTING_KEY);
    }


    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
