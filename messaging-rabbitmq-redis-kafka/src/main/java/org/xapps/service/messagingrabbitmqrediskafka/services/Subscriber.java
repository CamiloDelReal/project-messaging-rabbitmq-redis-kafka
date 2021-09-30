package org.xapps.service.messagingrabbitmqrediskafka.services;

public interface Subscriber {
    public void setConsumerListener(Consumer.Listener listener);
}
