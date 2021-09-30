package org.xapps.service.messagingrabbitmqrediskafka.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xapps.service.messagingrabbitmqrediskafka.dto.OrderStatus;

@Component
public class Consumer {

    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    private final Listener listener = new Listener() {
        @Override
        public void message(OrderStatus orderStatus) {
            logger.info("Order received {}", orderStatus);
        }
    };

    private final Subscriber subscriber;

    @Autowired
    public Consumer(Subscriber subscriber) {
        this.subscriber = subscriber;
        this.subscriber.setConsumerListener(listener);
    }

    public interface Listener {
        public void message(OrderStatus orderStatus);
    }
}
