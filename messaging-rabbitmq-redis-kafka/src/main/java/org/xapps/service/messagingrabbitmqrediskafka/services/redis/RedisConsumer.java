package org.xapps.service.messagingrabbitmqrediskafka.services.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;
import org.xapps.service.messagingrabbitmqrediskafka.dto.OrderStatus;
import org.xapps.service.messagingrabbitmqrediskafka.services.Consumer;
import org.xapps.service.messagingrabbitmqrediskafka.services.Subscriber;
import org.xapps.service.messagingrabbitmqrediskafka.utils.ProfileUtils;

@Component
@Profile(ProfileUtils.REDIS_PROFILE)
public class RedisConsumer implements Subscriber, MessageListener {

    private final Logger logger = LoggerFactory.getLogger(RedisConsumer.class);
    private Consumer.Listener listener;

    @Override
    public void setConsumerListener(Consumer.Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        logger.info("Message received {}", message);
        if(listener != null) {
            Jackson2JsonRedisSerializer<OrderStatus> deserializer = new Jackson2JsonRedisSerializer<OrderStatus>(OrderStatus.class);
            OrderStatus orderStatus = deserializer.deserialize(message.getBody());
            listener.message(orderStatus);
        }
    }
}
