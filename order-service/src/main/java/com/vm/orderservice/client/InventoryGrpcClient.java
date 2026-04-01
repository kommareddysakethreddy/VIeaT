package com.vm.orderservice.client;

import com.vm.inventoryservice.grpc.InventoryGrpcServiceGrpc;
import com.vm.inventoryservice.grpc.StockRequest;
import com.vm.inventoryservice.grpc.StockResponse;
import com.vm.orderservice.exception.InventoryOperationException;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class InventoryGrpcClient {

    @GrpcClient("inventory-service")
    private InventoryGrpcServiceGrpc.InventoryGrpcServiceBlockingStub inventoryStub;

    public StockResponse checkAndReduceStock(Long foodItemId, Integer quantity) {
        StockRequest request = StockRequest.newBuilder()
                .setFoodItemId(foodItemId)
                .setQuantity(quantity)
                .build();
        try {
            return inventoryStub.checkAndReduceStock(request);
        } catch (StatusRuntimeException ex) {
            throw new InventoryOperationException("Unable to contact inventory-service");
        }
    }

    public StockResponse restoreStock(Long foodItemId, Integer quantity) {
        StockRequest request = StockRequest.newBuilder()
                .setFoodItemId(foodItemId)
                .setQuantity(quantity)
                .build();
        try {
            return inventoryStub.restoreStock(request);
        } catch (StatusRuntimeException ex) {
            throw new InventoryOperationException("Unable to restore stock in inventory-service");
        }
    }
}
