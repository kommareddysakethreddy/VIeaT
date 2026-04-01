package com.vm.inventoryservice.dto;

public class StockUpdateResponse {

    private Long foodItemId;
    private String foodItemName;
    private Integer quantityAvailable;
    private Boolean isAvailable;
    private String message;

    public StockUpdateResponse() {
    }

    public StockUpdateResponse(Long foodItemId, String foodItemName, Integer quantityAvailable, Boolean isAvailable,
                               String message) {
        this.foodItemId = foodItemId;
        this.foodItemName = foodItemName;
        this.quantityAvailable = quantityAvailable;
        this.isAvailable = isAvailable;
        this.message = message;
    }

    public Long getFoodItemId() {
        return foodItemId;
    }

    public void setFoodItemId(Long foodItemId) {
        this.foodItemId = foodItemId;
    }

    public String getFoodItemName() {
        return foodItemName;
    }

    public void setFoodItemName(String foodItemName) {
        this.foodItemName = foodItemName;
    }

    public Integer getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(Integer quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean available) {
        isAvailable = available;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
