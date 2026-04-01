package com.vm.inventoryservice.service;

import com.vm.inventoryservice.dto.FoodItemRequest;
import com.vm.inventoryservice.dto.FoodItemResponse;
import com.vm.inventoryservice.dto.StockUpdateResponse;
import java.util.List;

public interface FoodItemService {

    FoodItemResponse createFoodItem(FoodItemRequest request);

    List<FoodItemResponse> getAllFoodItems();

    FoodItemResponse getFoodItemById(Long id);

    FoodItemResponse updateFoodItem(Long id, FoodItemRequest request);

    void deleteFoodItem(Long id);

    StockUpdateResponse addStock(Long id, Integer quantity);

    StockUpdateResponse reduceStock(Long id, Integer quantity);

    List<FoodItemResponse> getAvailableFoodItems();

    List<FoodItemResponse> getFoodItemsByCategory(String category);
}
