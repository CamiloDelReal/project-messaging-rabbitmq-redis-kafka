package org.xapps.service.messagingrabbitmqrediskafka.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.xapps.service.messagingrabbitmqrediskafka.dto.Order;
import org.xapps.service.messagingrabbitmqrediskafka.dto.OrderStatus;
import org.xapps.service.messagingrabbitmqrediskafka.dto.Status;
import org.xapps.service.messagingrabbitmqrediskafka.services.Publisher;

import java.util.UUID;

@RestController
@RequestMapping(path = "/messaging")
public class MessagingController {

    private final Publisher publisher;

    @Autowired
    public MessagingController(Publisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public String publish(@RequestBody Order order) {
        try {
            OrderStatus status = new OrderStatus(UUID.randomUUID().toString(), order, Status.IN_PROGRESS);
            publisher.publish(status);
            return "Order accepted";
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage());
        }
    }
}
