package org.xapps.service.messagingrabbitmqrediskafka.services.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.xapps.service.messagingrabbitmqrediskafka.dto.OrderStatus;
import org.xapps.service.messagingrabbitmqrediskafka.services.Publisher;
import org.xapps.service.messagingrabbitmqrediskafka.utils.ProfileUtils;

@Service
@Profile(ProfileUtils.KAFKA_PROFILE)
public class KafkaPublisher implements Publisher {

    private final Logger logger = LoggerFactory.getLogger(KafkaPublisher.class);
    @Value("${messaging.kafka.topic}")
    private String topic;
    private final KafkaTemplate<String, OrderStatus> kafkaTemplate;

    @Autowired
    public KafkaPublisher(KafkaTemplate<String, OrderStatus> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(OrderStatus orderStatus) {
        logger.info("Kafka publisher is about to publish {}", orderStatus);
        kafkaTemplate.send(topic, orderStatus);
    }
}
