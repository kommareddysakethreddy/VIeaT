package com.vm.orderservice.dto;

import java.math.BigDecimal;

public class OrderItemResponse {

    private Long id;
    private Long foodItemId;
    private String foodItemName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;

    public OrderItemResponse() {
    }

    public OrderItemResponse(Long id, Long foodItemId, String foodItemName, Integer quantity,
                             BigDecimal price, BigDecimal subtotal) {
        this.id = id;
        this.foodItemId = foodItemId;
        this.foodItemName = foodItemName;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
