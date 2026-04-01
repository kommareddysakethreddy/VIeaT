package com.vm.notificationservice.kafka;

import com.vm.notificationservice.dto.OrderPlacedEvent;
import com.vm.notificationservice.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderPlacedEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderPlacedEventConsumer.class);

    private final NotificationService notificationService;

    public OrderPlacedEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "${app.kafka.order-placed-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(OrderPlacedEvent event) {
        // notification-service also reacts to the same order event and stores a simple notification.
        log.info("Received order-placed event in notification-service for orderId={}, customerId={}",
                event.getOrderId(), event.getCustomerId());
        notificationService.processOrderPlacedEvent(event);
    }
}
