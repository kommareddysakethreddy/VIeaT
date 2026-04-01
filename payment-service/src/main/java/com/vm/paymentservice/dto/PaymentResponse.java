package com.vm.paymentservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {

    private final Long id;
    private final Long orderId;
    private final Long customerId;
    private final BigDecimal amount;
    private final String status;
    private final LocalDateTime createdAt;

    public PaymentResponse(Long id, Long orderId, Long customerId, BigDecimal amount, String status, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
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

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
