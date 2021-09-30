package org.xapps.service.messagingrabbitmqrediskafka.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderStatus {
    private String id;
    private Order order;
    private Status status;
}
