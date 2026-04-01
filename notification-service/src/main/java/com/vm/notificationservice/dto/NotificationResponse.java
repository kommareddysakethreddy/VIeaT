package com.vm.notificationservice.dto;

import java.time.LocalDateTime;

public class NotificationResponse {

    private final Long id;
    private final Long orderId;
    private final Long customerId;
    private final String message;
    private final String type;
    private final String status;
    private final LocalDateTime createdAt;

    public NotificationResponse(Long id, Long orderId, Long customerId, String message, String type, String status,
                                LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.customerId = customerId;
        this.message = message;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
