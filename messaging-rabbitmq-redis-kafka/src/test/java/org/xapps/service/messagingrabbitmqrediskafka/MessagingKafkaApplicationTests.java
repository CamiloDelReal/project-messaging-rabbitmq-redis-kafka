package org.xapps.service.messagingrabbitmqrediskafka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.xapps.service.messagingrabbitmqrediskafka.dto.Order;
import org.xapps.service.messagingrabbitmqrediskafka.dto.OrderStatus;
import org.xapps.service.messagingrabbitmqrediskafka.dto.Status;
import org.xapps.service.messagingrabbitmqrediskafka.services.Consumer;
import org.xapps.service.messagingrabbitmqrediskafka.services.kafka.KafkaConsumer;
import org.xapps.service.messagingrabbitmqrediskafka.services.kafka.KafkaPublisher;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("kafka")
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class MessagingKafkaApplicationTests {

    @Autowired
    private KafkaConsumer consumer;

    @Autowired
    private KafkaPublisher publisher;

    private CountDownLatch latch = new CountDownLatch(1);

    private OrderStatus result = null;

    private Consumer.Listener listener = orderStatus -> {
        result = orderStatus;
        latch.countDown();
    };

    @DynamicPropertySource
    public static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("messaging.kafka.host", () -> "localhost");
        registry.add("messaging.kafka.port", () -> 9092 );
    }

    @Test
    public void kafkaMessagging_success()
            throws Exception {
        Order order = new Order("Capuchino", 2);
        OrderStatus orderStatus = new OrderStatus(
                UUID.randomUUID().toString(),
                order,
                Status.IN_PROGRESS
        );

        consumer.setConsumerListener(listener);
        publisher.publish(orderStatus);
        latch.await(10, TimeUnit.SECONDS);

        assertNotNull(result);
        assertEquals(orderStatus.getId(), result.getId());
        assertEquals(orderStatus.getStatus(), result.getStatus());
        assertEquals(orderStatus.getOrder().getName(), result.getOrder().getName());
        assertEquals(orderStatus.getOrder().getQuantity(), result.getOrder().getQuantity());
    }
}
