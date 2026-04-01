package com.vm.inventoryservice.service.impl;

import com.vm.inventoryservice.dto.FoodItemRequest;
import com.vm.inventoryservice.dto.FoodItemResponse;
import com.vm.inventoryservice.dto.StockUpdateResponse;
import com.vm.inventoryservice.entity.FoodItem;
import com.vm.inventoryservice.exception.InsufficientStockException;
import com.vm.inventoryservice.exception.ResourceNotFoundException;
import com.vm.inventoryservice.repository.FoodItemRepository;
import com.vm.inventoryservice.service.FoodItemService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class FoodItemServiceImpl implements FoodItemService {

    private final FoodItemRepository foodItemRepository;

    public FoodItemServiceImpl(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    @Override
    public FoodItemResponse createFoodItem(FoodItemRequest request) {
        FoodItem foodItem = buildFoodItemFromRequest(new FoodItem(), request);
        return mapToResponse(foodItemRepository.save(foodItem));
    }

    @Override
    public List<FoodItemResponse> getAllFoodItems() {
        return foodItemRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FoodItemResponse getFoodItemById(Long id) {
        return mapToResponse(getFoodItemEntityById(id));
    }

    @Override
    public FoodItemResponse updateFoodItem(Long id, FoodItemRequest request) {
        FoodItem existingFoodItem = getFoodItemEntityById(id);
        buildFoodItemFromRequest(existingFoodItem, request);
        return mapToResponse(foodItemRepository.save(existingFoodItem));
    }

    @Override
    public void deleteFoodItem(Long id) {
        FoodItem foodItem = getFoodItemEntityById(id);
        foodItemRepository.delete(foodItem);
    }

    @Override
    public StockUpdateResponse addStock(Long id, Integer quantity) {
        validateStockQuantity(quantity);
        FoodItem foodItem = getFoodItemEntityById(id);
        foodItem.setQuantityAvailable(foodItem.getQuantityAvailable() + quantity);
        foodItem.updateAvailability();
        FoodItem updatedFoodItem = foodItemRepository.save(foodItem);
        return buildStockResponse(updatedFoodItem, "Stock added successfully");
    }

    @Override
    public StockUpdateResponse reduceStock(Long id, Integer quantity) {
        validateStockQuantity(quantity);
        FoodItem foodItem = getFoodItemEntityById(id);

        if (foodItem.getQuantityAvailable() < quantity) {
            throw new InsufficientStockException("Not enough stock available for food item id " + id);
        }

        foodItem.setQuantityAvailable(foodItem.getQuantityAvailable() - quantity);
        foodItem.updateAvailability();
        FoodItem updatedFoodItem = foodItemRepository.save(foodItem);
        return buildStockResponse(updatedFoodItem, "Stock reduced successfully");
    }

    @Override
    public List<FoodItemResponse> getAvailableFoodItems() {
        return foodItemRepository.findByIsAvailableTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FoodItemResponse> getFoodItemsByCategory(String category) {
        return foodItemRepository.findByCategoryIgnoreCase(category)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private FoodItem getFoodItemEntityById(Long id) {
        return foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id: " + id));
    }

    private FoodItem buildFoodItemFromRequest(FoodItem foodItem, FoodItemRequest request) {
        foodItem.setName(request.getName());
        foodItem.setDescription(request.getDescription());
        foodItem.setCategory(request.getCategory());
        foodItem.setPrice(request.getPrice());
        foodItem.setQuantityAvailable(request.getQuantityAvailable());
        foodItem.updateAvailability();
        return foodItem;
    }

    private FoodItemResponse mapToResponse(FoodItem foodItem) {
        return new FoodItemResponse(
                foodItem.getId(),
                foodItem.getName(),
                foodItem.getDescription(),
                foodItem.getCategory(),
                foodItem.getPrice(),
                foodItem.getQuantityAvailable(),
                foodItem.getIsAvailable(),
                foodItem.getCreatedAt(),
                foodItem.getUpdatedAt()
        );
    }

    private StockUpdateResponse buildStockResponse(FoodItem foodItem, String message) {
        return new StockUpdateResponse(
                foodItem.getId(),
                foodItem.getName(),
                foodItem.getQuantityAvailable(),
                foodItem.getIsAvailable(),
                message
        );
    }

    private void validateStockQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }
}
