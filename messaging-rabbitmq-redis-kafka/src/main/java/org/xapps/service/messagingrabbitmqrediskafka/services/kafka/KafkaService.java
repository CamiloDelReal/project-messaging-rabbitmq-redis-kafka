package org.xapps.service.messagingrabbitmqrediskafka.services.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.xapps.service.messagingrabbitmqrediskafka.dto.OrderStatus;
import org.xapps.service.messagingrabbitmqrediskafka.utils.ProfileUtils;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile(ProfileUtils.KAFKA_PROFILE)
@EnableKafka
public class KafkaService {

    private final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    @Value("${messaging.kafka.host}")
    private String host;
    @Value("${messaging.kafka.port}")
    private int port;
    @Value("${messaging.kafka.reset-config}")
    private String resetConfig;
    @Value("${messaging.kafka.topic}")
    private String topic;
    @Value("${messaging.kafka.group}")
    private String group;

    @Bean
    public ProducerFactory<String, OrderStatus> provideProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, String.format("%s:%d", host, port));
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ConsumerFactory<String, OrderStatus> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, String.format("%s:%d", host, port));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, resetConfig);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(OrderStatus.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderStatus> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderStatus> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public KafkaTemplate<String, OrderStatus> kafkaTemplate(ProducerFactory<String, OrderStatus> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

}
