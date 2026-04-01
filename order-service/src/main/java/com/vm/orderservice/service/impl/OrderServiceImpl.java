package com.vm.orderservice.service.impl;

import com.vm.inventoryservice.grpc.StockResponse;
import com.vm.orderservice.client.InventoryGrpcClient;
import com.vm.orderservice.dto.OrderItemRequest;
import com.vm.orderservice.dto.OrderItemResponse;
import com.vm.orderservice.dto.OrderRequest;
import com.vm.orderservice.dto.OrderResponse;
import com.vm.orderservice.dto.OrderStatusUpdateResponse;
import com.vm.orderservice.entity.Order;
import com.vm.orderservice.entity.OrderItem;
import com.vm.orderservice.exception.AccessDeniedException;
import com.vm.orderservice.exception.InventoryOperationException;
import com.vm.orderservice.exception.InvalidOrderStatusException;
import com.vm.orderservice.exception.ResourceNotFoundException;
import com.vm.orderservice.repository.OrderRepository;
import com.vm.orderservice.security.RequestIdentity;
import com.vm.orderservice.service.OrderEventPublisher;
import com.vm.orderservice.service.OrderService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Set<String> VALID_ORDER_STATUSES = Set.of(
            "PLACED",
            "PREPARING",
            "OUT_FOR_DELIVERY",
            "DELIVERED",
            "CANCELLED"
    );

    private final OrderRepository orderRepository;
    private final InventoryGrpcClient inventoryGrpcClient;
    private final OrderEventPublisher orderEventPublisher;

    public OrderServiceImpl(OrderRepository orderRepository, InventoryGrpcClient inventoryGrpcClient,
                            OrderEventPublisher orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.inventoryGrpcClient = inventoryGrpcClient;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request, RequestIdentity requestIdentity) {
        List<OrderItemRequest> reducedItems = new ArrayList<>();
        Long resolvedCustomerId = resolveCustomerId(request, requestIdentity);

        try {
            for (OrderItemRequest itemRequest : request.getItems()) {
                StockResponse stockResponse = inventoryGrpcClient.checkAndReduceStock(
                        itemRequest.getFoodItemId(),
                        itemRequest.getQuantity()
                );

                if (!stockResponse.getSuccess()) {
                    safelyRestoreReducedStock(reducedItems);
                    throw new InventoryOperationException(stockResponse.getMessage());
                }

                reducedItems.add(itemRequest);
            }

            Order order = new Order();
            order.setCustomerId(resolvedCustomerId);
            order.setCustomerName(request.getCustomerName());
            order.setDeliveryAddress(request.getDeliveryAddress());
            order.setOrderStatus("PLACED");
            order.setOwnerUserId(requestIdentity.userId());
            order.setOwnerEmail(requestIdentity.email());

            List<OrderItem> orderItems = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (OrderItemRequest itemRequest : request.getItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setFoodItemId(itemRequest.getFoodItemId());
                orderItem.setFoodItemName(itemRequest.getFoodItemName());
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItem.setPrice(itemRequest.getPrice());

                BigDecimal subtotal = itemRequest.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
                orderItem.setSubtotal(subtotal);
                orderItem.setOrder(order);

                orderItems.add(orderItem);
                totalAmount = totalAmount.add(subtotal);
            }

            order.setItems(orderItems);
            order.setTotalAmount(totalAmount);

            Order savedOrder = orderRepository.save(order);
            // Publish the event only after the order entity is prepared successfully.
            orderEventPublisher.publishOrderPlacedEvent(savedOrder);
            return mapToResponse(savedOrder);
        } catch (RuntimeException ex) {
            if (!(ex instanceof InventoryOperationException)) {
                safelyRestoreReducedStock(reducedItems);
            }
            throw ex;
        }
    }

    @Override
    public List<OrderResponse> getAllOrders(RequestIdentity requestIdentity) {
        List<Order> orders = requestIdentity.isAdmin()
                ? orderRepository.findAll()
                : orderRepository.findByOwnerUserIdOrderByCreatedAtDesc(requestIdentity.userId());

        return orders
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long id, RequestIdentity requestIdentity) {
        Order order = getOrderEntityById(id);
        validateOwnership(order, requestIdentity);
        return mapToResponse(order);
    }

    @Override
    public List<OrderResponse> getOrdersByCustomerId(Long customerId, RequestIdentity requestIdentity) {
        if (!requestIdentity.isAdmin() && !customerId.equals(requestIdentity.customerId())) {
            throw new AccessDeniedException("You are not allowed to access another customer's orders");
        }

        return orderRepository.findByCustomerId(customerId)
                .stream()
                .filter(order -> requestIdentity.isAdmin() || requestIdentity.userId().equals(order.getOwnerUserId()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderStatusUpdateResponse updateOrderStatus(Long id, String statusValue, RequestIdentity requestIdentity) {
        requireAdmin(requestIdentity, "Only administrators can update order status");
        String normalizedStatus = normalizeAndValidateStatus(statusValue);
        Order order = getOrderEntityById(id);
        order.setOrderStatus(normalizedStatus);
        Order updatedOrder = orderRepository.save(order);

        return new OrderStatusUpdateResponse(
                updatedOrder.getId(),
                updatedOrder.getOrderStatus(),
                "Order status updated successfully"
        );
    }

    @Override
    public void deleteOrder(Long id, RequestIdentity requestIdentity) {
        requireAdmin(requestIdentity, "Only administrators can delete orders");
        Order order = getOrderEntityById(id);
        orderRepository.delete(order);
    }

    private Order getOrderEntityById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private String normalizeAndValidateStatus(String statusValue) {
        if (statusValue == null || statusValue.isBlank()) {
            throw new InvalidOrderStatusException("Order status is required");
        }

        String normalizedStatus = statusValue.trim().toUpperCase();
        if (!VALID_ORDER_STATUSES.contains(normalizedStatus)) {
            throw new InvalidOrderStatusException("Invalid order status: " + statusValue);
        }
        return normalizedStatus;
    }

    private void validateOwnership(Order order, RequestIdentity requestIdentity) {
        if (!requestIdentity.isAdmin() && !requestIdentity.userId().equals(order.getOwnerUserId())) {
            throw new AccessDeniedException("You are not allowed to access this order");
        }
    }

    private void requireAdmin(RequestIdentity requestIdentity, String message) {
        if (!requestIdentity.isAdmin()) {
            throw new AccessDeniedException(message);
        }
    }

    private Long resolveCustomerId(OrderRequest request, RequestIdentity requestIdentity) {
        if (requestIdentity.isAdmin()) {
            if (request.getCustomerId() == null) {
                throw new IllegalArgumentException("Customer id is required");
            }
            return request.getCustomerId();
        }

        if (requestIdentity.customerId() == null) {
            throw new AccessDeniedException("Customer account is not linked to a customer id");
        }

        return requestIdentity.customerId();
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems()
                .stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getCustomerName(),
                order.getDeliveryAddress(),
                order.getOrderStatus(),
                order.getTotalAmount(),
                itemResponses,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    private OrderItemResponse mapItemToResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getFoodItemId(),
                item.getFoodItemName(),
                item.getQuantity(),
                item.getPrice(),
                item.getSubtotal()
        );
    }

    private void safelyRestoreReducedStock(List<OrderItemRequest> reducedItems) {
        for (int i = reducedItems.size() - 1; i >= 0; i--) {
            OrderItemRequest itemRequest = reducedItems.get(i);
            try {
                inventoryGrpcClient.restoreStock(itemRequest.getFoodItemId(), itemRequest.getQuantity());
            } catch (RuntimeException ignored) {
                // Keep the original order failure response instead of masking it with rollback errors.
            }
        }
    }
}
