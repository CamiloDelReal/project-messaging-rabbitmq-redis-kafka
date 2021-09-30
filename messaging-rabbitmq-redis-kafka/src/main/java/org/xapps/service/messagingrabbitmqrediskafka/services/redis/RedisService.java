package org.xapps.service.messagingrabbitmqrediskafka.services.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.xapps.service.messagingrabbitmqrediskafka.utils.ProfileUtils;

@Configuration
@Profile(ProfileUtils.REDIS_PROFILE)
public class RedisService {

    private final Logger logger = LoggerFactory.getLogger(RedisService.class);

    @Value("${messaging.redis.host}")
    private String host;
    @Value("${messaging.redis.port}")
    private int port;
    @Value("${messaging.redis.username}")
    private String username;
    @Value("${messaging.redis.password}")
    private String password;
    @Value("${messaging.redis.topic}")
    private String topic;

    @Bean
    public JedisConnectionFactory provideJedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setUsername(username);
        configuration.setPassword(password);
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    public ChannelTopic provideChannelTopic() {
        return new ChannelTopic(topic);
    }

    @Bean
    public MessageListenerAdapter provideMessageListenerAdapter(MessageListener messageListener) {
        return new MessageListenerAdapter(messageListener); // Taken from RedisConsumer who inheerit from MessageListener
    }

    @Bean
    public RedisMessageListenerContainer provideRedisMessageListenerContainer(JedisConnectionFactory connectionFactory, MessageListenerAdapter messageListenerAdapter, ChannelTopic channelTopic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListenerAdapter, channelTopic);
        return container;
    }

    @Bean
    public RedisTemplate<String, Object> provideRedisTemplate(JedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
        return template;
    }

}
