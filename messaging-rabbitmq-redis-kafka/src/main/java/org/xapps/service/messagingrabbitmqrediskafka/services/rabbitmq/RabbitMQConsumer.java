package org.xapps.service.messagingrabbitmqrediskafka.services.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.xapps.service.messagingrabbitmqrediskafka.dto.OrderStatus;
import org.xapps.service.messagingrabbitmqrediskafka.services.Consumer;
import org.xapps.service.messagingrabbitmqrediskafka.services.Subscriber;
import org.xapps.service.messagingrabbitmqrediskafka.utils.ProfileUtils;

@Component
@Profile(ProfileUtils.RABBITMQ_PROFILE)
public class RabbitMQConsumer implements Subscriber {

    private final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);
    private Consumer.Listener listener;

    @Override
    public void setConsumerListener(Consumer.Listener listener) {
        this.listener = listener;
    }

    @RabbitListener(queues = "${messaging.rabbitmq.queue}")
    public void processMessage(OrderStatus orderStatus) {
        logger.info("Message received {}", orderStatus);
        if(listener != null) {
            listener.message(orderStatus);
        }
    }
}
