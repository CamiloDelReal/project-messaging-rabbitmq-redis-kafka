package org.xapps.service.messagingrabbitmqrediskafka.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {
    private String name;
    private int quantity;
}
