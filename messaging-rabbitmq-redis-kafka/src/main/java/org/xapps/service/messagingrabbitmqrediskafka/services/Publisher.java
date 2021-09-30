package org.xapps.service.messagingrabbitmqrediskafka.services;

import org.xapps.service.messagingrabbitmqrediskafka.dto.OrderStatus;

public interface Publisher {

    void publish(OrderStatus orderStatus);

}
