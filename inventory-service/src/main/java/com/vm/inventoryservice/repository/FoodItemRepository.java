package com.vm.inventoryservice.repository;

import com.vm.inventoryservice.entity.FoodItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    List<FoodItem> findByIsAvailableTrue();

    List<FoodItem> findByCategoryIgnoreCase(String category);
}
