package com.vm.inventoryservice.grpc;

import com.vm.inventoryservice.entity.FoodItem;
import com.vm.inventoryservice.repository.FoodItemRepository;
import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class InventoryGrpcServer extends InventoryGrpcServiceGrpc.InventoryGrpcServiceImplBase {

    private final FoodItemRepository foodItemRepository;

    public InventoryGrpcServer(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    @Override
    @Transactional
    public void checkAndReduceStock(StockRequest request, StreamObserver<StockResponse> responseObserver) {
        StockResponse response;

        if (request.getQuantity() <= 0) {
            response = buildFailureResponse(request.getFoodItemId(), "Quantity must be greater than 0");
        } else {
            FoodItem foodItem = foodItemRepository.findById(request.getFoodItemId()).orElse(null);

            if (foodItem == null) {
                response = buildFailureResponse(request.getFoodItemId(), "Food item not found");
            } else if (foodItem.getQuantityAvailable() < request.getQuantity()) {
                response = StockResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Insufficient stock for food item: " + foodItem.getName())
                        .setFoodItemId(foodItem.getId())
                        .setFoodItemName(foodItem.getName())
                        .setRemainingQuantity(foodItem.getQuantityAvailable())
                        .build();
            } else {
                foodItem.setQuantityAvailable(foodItem.getQuantityAvailable() - request.getQuantity());
                foodItem.updateAvailability();
                FoodItem updatedItem = foodItemRepository.save(foodItem);

                response = StockResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("Stock reduced successfully")
                        .setFoodItemId(updatedItem.getId())
                        .setFoodItemName(updatedItem.getName())
                        .setRemainingQuantity(updatedItem.getQuantityAvailable())
                        .build();
            }
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void restoreStock(StockRequest request, StreamObserver<StockResponse> responseObserver) {
        StockResponse response;

        if (request.getQuantity() <= 0) {
            response = buildFailureResponse(request.getFoodItemId(), "Quantity must be greater than 0");
        } else {
            FoodItem foodItem = foodItemRepository.findById(request.getFoodItemId()).orElse(null);

            if (foodItem == null) {
                response = buildFailureResponse(request.getFoodItemId(), "Food item not found");
            } else {
                foodItem.setQuantityAvailable(foodItem.getQuantityAvailable() + request.getQuantity());
                foodItem.updateAvailability();
                FoodItem updatedItem = foodItemRepository.save(foodItem);

                response = StockResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("Stock restored successfully")
                        .setFoodItemId(updatedItem.getId())
                        .setFoodItemName(updatedItem.getName())
                        .setRemainingQuantity(updatedItem.getQuantityAvailable())
                        .build();
            }
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private StockResponse buildFailureResponse(Long foodItemId, String message) {
        return StockResponse.newBuilder()
                .setSuccess(false)
                .setMessage(message)
                .setFoodItemId(foodItemId == null ? 0L : foodItemId)
                .setFoodItemName("")
                .setRemainingQuantity(0)
                .build();
    }
}
