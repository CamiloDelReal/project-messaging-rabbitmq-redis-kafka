package org.xapps.service.messagingrabbitmqrediskafka.services.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.xapps.service.messagingrabbitmqrediskafka.dto.OrderStatus;
import org.xapps.service.messagingrabbitmqrediskafka.services.Publisher;
import org.xapps.service.messagingrabbitmqrediskafka.utils.ProfileUtils;

@Service
@Profile(ProfileUtils.RABBITMQ_PROFILE)
public class RabbitMQPublisher implements Publisher {

    private final Logger logger = LoggerFactory.getLogger(RabbitMQPublisher.class);
    private final RabbitTemplate rabbitTemplate;
    @Value("${messaging.rabbitmq.exchange}")
    private String exchange;
    @Value("${messaging.rabbitmq.routing-key}")
    private String routingKey;

    @Autowired
    public RabbitMQPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(OrderStatus orderStatus) {
        logger.info("RabbitMQ publisher is about to publish {}", orderStatus);
        rabbitTemplate.convertAndSend(exchange, routingKey, orderStatus);
    }

}
