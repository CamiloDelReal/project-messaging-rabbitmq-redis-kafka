package org.xapps.service.messagingrabbitmqrediskafka.services.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.xapps.service.messagingrabbitmqrediskafka.utils.ProfileUtils;

@Configuration
@Profile(ProfileUtils.RABBITMQ_PROFILE)
public class RabbitMQService {

    private final Logger logger = LoggerFactory.getLogger(RabbitMQService.class);

    @Value("${messaging.rabbitmq.host}")
    private String host;
    @Value("${messaging.rabbitmq.port}")
    private int port;
    @Value("${messaging.rabbitmq.username}")
    private String username;
    @Value("${messaging.rabbitmq.password}")
    private String password;
    @Value("${messaging.rabbitmq.queue}")
    private String queue;
    @Value("${messaging.rabbitmq.exchange}")
    private String exchange;
    @Value("${messaging.rabbitmq.routing-key}")
    private String routingKey;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    public Queue provideQueue() {
        return new Queue(queue);
    }

    @Bean
    public TopicExchange provideExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding provideBinding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    public MessageConverter provideMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate provideTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

}
