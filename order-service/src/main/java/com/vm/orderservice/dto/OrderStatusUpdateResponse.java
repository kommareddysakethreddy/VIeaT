package com.vm.orderservice.dto;

public class OrderStatusUpdateResponse {

    private Long orderId;
    private String orderStatus;
    private String message;

    public OrderStatusUpdateResponse() {
    }

    public OrderStatusUpdateResponse(Long orderId, String orderStatus, String message) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.message = message;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
