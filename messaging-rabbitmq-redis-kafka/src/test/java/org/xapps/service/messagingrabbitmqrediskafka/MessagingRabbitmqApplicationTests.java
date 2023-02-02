package org.xapps.service.messagingrabbitmqrediskafka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.xapps.service.messagingrabbitmqrediskafka.dto.Order;
import org.xapps.service.messagingrabbitmqrediskafka.dto.OrderStatus;
import org.xapps.service.messagingrabbitmqrediskafka.dto.Status;
import org.xapps.service.messagingrabbitmqrediskafka.services.Consumer;
import org.xapps.service.messagingrabbitmqrediskafka.services.rabbitmq.RabbitMQConsumer;
import org.xapps.service.messagingrabbitmqrediskafka.services.rabbitmq.RabbitMQPublisher;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("rabbitmq")
@DirtiesContext
@Testcontainers
public class MessagingRabbitmqApplicationTests {

    @Container
    static RabbitMQContainer rabbitContainer = new RabbitMQContainer("rabbitmq:3.7.25-management-alpine")
            .withExposedPorts(5672)
            .withUser("guest", "guest");

    @Autowired
    private RabbitMQConsumer consumer;

    @Autowired
    private RabbitMQPublisher publisher;

    private CountDownLatch latch = new CountDownLatch(1);

    private OrderStatus result = null;

    private Consumer.Listener listener = orderStatus -> {
        result = orderStatus;
        latch.countDown();
    };

    @DynamicPropertySource
    public static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("messaging.rabbitmq.host", () -> "localhost");
        registry.add("messaging.rabbitmq.port", () -> rabbitContainer.getMappedPort(5672));
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
