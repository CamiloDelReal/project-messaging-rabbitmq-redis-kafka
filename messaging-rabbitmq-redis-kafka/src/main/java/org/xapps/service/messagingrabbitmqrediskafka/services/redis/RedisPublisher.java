package org.xapps.service.messagingrabbitmqrediskafka.services.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.xapps.service.messagingrabbitmqrediskafka.dto.OrderStatus;
import org.xapps.service.messagingrabbitmqrediskafka.services.Publisher;
import org.xapps.service.messagingrabbitmqrediskafka.utils.ProfileUtils;

@Service
@Profile(ProfileUtils.REDIS_PROFILE)
public class RedisPublisher implements Publisher {

    private final Logger logger = LoggerFactory.getLogger(RedisPublisher.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    @Autowired
    public RedisPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic channelTopic) {
        this.redisTemplate = redisTemplate;
        this.channelTopic = channelTopic;
    }

    @Override
    public void publish(OrderStatus orderStatus) {
        logger.info("Redis publisher is about to publish {}", orderStatus);
        redisTemplate.convertAndSend(channelTopic.getTopic(), orderStatus);
    }
}
