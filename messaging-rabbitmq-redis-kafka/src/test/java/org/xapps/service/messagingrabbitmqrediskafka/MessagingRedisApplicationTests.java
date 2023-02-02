package org.xapps.service.messagingrabbitmqrediskafka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.xapps.service.messagingrabbitmqrediskafka.dto.Order;
import org.xapps.service.messagingrabbitmqrediskafka.dto.OrderStatus;
import org.xapps.service.messagingrabbitmqrediskafka.dto.Status;
import org.xapps.service.messagingrabbitmqrediskafka.services.Consumer;
import org.xapps.service.messagingrabbitmqrediskafka.services.redis.RedisConsumer;
import org.xapps.service.messagingrabbitmqrediskafka.services.redis.RedisPublisher;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("redis")
@DirtiesContext
@Testcontainers
public class MessagingRedisApplicationTests {

    @Container
    private static GenericContainer<?> redisContainer = new GenericContainer(DockerImageName.parse("redis:5.0.3-alpine"))
        .withReuse(true)
        .withExposedPorts(6379);

    @Autowired
    private RedisConsumer consumer;

    @Autowired
    private RedisPublisher publisher;

    private CountDownLatch latch = new CountDownLatch(1);

    private OrderStatus result = null;

    private Consumer.Listener listener = orderStatus -> {
        result = orderStatus;
        latch.countDown();
    };

    @DynamicPropertySource
    public static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("messaging.redis.host", () -> "localhost");
        registry.add("messaging.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Test
    public void rabbitmqMessaging_success()
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
