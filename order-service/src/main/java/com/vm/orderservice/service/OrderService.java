package com.vm.orderservice.service;

import com.vm.orderservice.dto.OrderRequest;
import com.vm.orderservice.dto.OrderResponse;
import com.vm.orderservice.dto.OrderStatusUpdateResponse;
import com.vm.orderservice.security.RequestIdentity;
import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request, RequestIdentity requestIdentity);

    List<OrderResponse> getAllOrders(RequestIdentity requestIdentity);

    OrderResponse getOrderById(Long id, RequestIdentity requestIdentity);

    List<OrderResponse> getOrdersByCustomerId(Long customerId, RequestIdentity requestIdentity);

    OrderStatusUpdateResponse updateOrderStatus(Long id, String statusValue, RequestIdentity requestIdentity);

    void deleteOrder(Long id, RequestIdentity requestIdentity);
}
