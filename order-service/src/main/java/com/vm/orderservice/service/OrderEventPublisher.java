package com.vm.orderservice.service;

import com.vm.orderservice.dto.OrderPlacedEvent;
import com.vm.orderservice.entity.Order;
import com.vm.orderservice.exception.OrderEventPublishException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Value("${app.kafka.order-placed-topic}")
    private String orderPlacedTopic;

    public OrderEventPublisher(KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderPlacedEvent(Order order) {
        OrderPlacedEvent event = new OrderPlacedEvent(
                order.getId(),
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getOrderStatus(),
                order.getCreatedAt(),
                order.getOwnerUserId(),
                order.getOwnerEmail()
        );

        try {
            log.info("Publishing order-placed event for orderId={}, customerId={}, amount={}",
                    order.getId(), order.getCustomerId(), order.getTotalAmount());
            // Waiting for send completion keeps the flow easy to understand during learning and local testing.
            kafkaTemplate.send(orderPlacedTopic, order.getId().toString(), event).get(10, TimeUnit.SECONDS);
            log.info("Published order-placed event to topic={} for orderId={}", orderPlacedTopic, order.getId());
        } catch (Exception ex) {
            throw new OrderEventPublishException("Failed to publish order-placed event for order id: " + order.getId(), ex);
        }
    }
}
