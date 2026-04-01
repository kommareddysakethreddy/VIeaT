package com.vm.orderservice.exception;

public class InventoryOperationException extends RuntimeException {

    public InventoryOperationException(String message) {
        super(message);
    }
}
