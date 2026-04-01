package com.vm.inventoryservice.controller;

import com.vm.inventoryservice.dto.FoodItemRequest;
import com.vm.inventoryservice.dto.FoodItemResponse;
import com.vm.inventoryservice.dto.StockUpdateResponse;
import com.vm.inventoryservice.service.FoodItemService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/food-items", "/api/food-items"})
@Validated
public class FoodItemController {

    private final FoodItemService foodItemService;

    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    @PostMapping
    public ResponseEntity<FoodItemResponse> createFoodItem(@Valid @RequestBody FoodItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(foodItemService.createFoodItem(request));
    }

    @GetMapping
    public ResponseEntity<List<FoodItemResponse>> getAllFoodItems() {
        return ResponseEntity.ok(foodItemService.getAllFoodItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodItemResponse> getFoodItemById(@PathVariable Long id) {
        return ResponseEntity.ok(foodItemService.getFoodItemById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodItemResponse> updateFoodItem(
            @PathVariable Long id,
            @Valid @RequestBody FoodItemRequest request) {
        return ResponseEntity.ok(foodItemService.updateFoodItem(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable Long id) {
        foodItemService.deleteFoodItem(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/add-stock")
    public ResponseEntity<StockUpdateResponse> addStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(foodItemService.addStock(id, quantity));
    }

    @PatchMapping("/{id}/reduce-stock")
    public ResponseEntity<StockUpdateResponse> reduceStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(foodItemService.reduceStock(id, quantity));
    }

    @GetMapping("/available")
    public ResponseEntity<List<FoodItemResponse>> getAvailableFoodItems() {
        return ResponseEntity.ok(foodItemService.getAvailableFoodItems());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<FoodItemResponse>> getFoodItemsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(foodItemService.getFoodItemsByCategory(category));
    }
}
