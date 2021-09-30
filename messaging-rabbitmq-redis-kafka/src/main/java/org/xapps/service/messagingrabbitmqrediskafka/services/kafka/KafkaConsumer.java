package org.xapps.service.messagingrabbitmqrediskafka.services.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.xapps.service.messagingrabbitmqrediskafka.dto.OrderStatus;
import org.xapps.service.messagingrabbitmqrediskafka.services.Consumer;
import org.xapps.service.messagingrabbitmqrediskafka.services.Subscriber;
import org.xapps.service.messagingrabbitmqrediskafka.utils.ProfileUtils;

@Component
@Profile(ProfileUtils.KAFKA_PROFILE)
public class KafkaConsumer implements Subscriber {

    private final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private Consumer.Listener listener;

    @Override
    public void setConsumerListener(Consumer.Listener listener) {
        this.listener = listener;
    }

    @KafkaListener(topics = "${messaging.kafka.topic}", groupId = "${messaging.kafka.group}")
    public void processMessage(OrderStatus orderStatus) {
        logger.info("Consumed event {}", orderStatus);
        if(listener != null) {
            listener.message(orderStatus);
        }
    }
}
