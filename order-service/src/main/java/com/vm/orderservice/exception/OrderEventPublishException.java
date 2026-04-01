package com.vm.orderservice.exception;

public class OrderEventPublishException extends RuntimeException {

    public OrderEventPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
