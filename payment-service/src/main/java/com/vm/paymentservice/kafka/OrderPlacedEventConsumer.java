package com.vm.paymentservice.kafka;

import com.vm.paymentservice.dto.OrderPlacedEvent;
import com.vm.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderPlacedEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderPlacedEventConsumer.class);

    private final PaymentService paymentService;

    public OrderPlacedEventConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "${app.kafka.order-placed-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(OrderPlacedEvent event) {
        // payment-service reacts to the order event and stores a simple payment record.
        log.info("Received order-placed event in payment-service for orderId={}, customerId={}, amount={}",
                event.getOrderId(), event.getCustomerId(), event.getAmount());
        paymentService.processOrderPlacedEvent(event);
    }
}
