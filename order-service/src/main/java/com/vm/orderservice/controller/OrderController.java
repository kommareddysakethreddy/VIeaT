package com.vm.orderservice.controller;

import com.vm.orderservice.dto.OrderRequest;
import com.vm.orderservice.dto.OrderResponse;
import com.vm.orderservice.dto.OrderStatusUpdateResponse;
import com.vm.orderservice.security.RequestIdentity;
import com.vm.orderservice.security.RequestIdentityResolver;
import jakarta.servlet.http.HttpServletRequest;
import com.vm.orderservice.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final RequestIdentityResolver requestIdentityResolver;

    public OrderController(OrderService orderService, RequestIdentityResolver requestIdentityResolver) {
        this.orderService = orderService;
        this.requestIdentityResolver = requestIdentityResolver;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request,
                                                     HttpServletRequest httpServletRequest) {
        RequestIdentity requestIdentity = requestIdentityResolver.resolve(httpServletRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request, requestIdentity));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(HttpServletRequest httpServletRequest) {
        RequestIdentity requestIdentity = requestIdentityResolver.resolve(httpServletRequest);
        return ResponseEntity.ok(orderService.getAllOrders(requestIdentity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        RequestIdentity requestIdentity = requestIdentityResolver.resolve(httpServletRequest);
        return ResponseEntity.ok(orderService.getOrderById(id, requestIdentity));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerId(@PathVariable Long customerId,
                                                                     HttpServletRequest httpServletRequest) {
        RequestIdentity requestIdentity = requestIdentityResolver.resolve(httpServletRequest);
        return ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId, requestIdentity));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderStatusUpdateResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam("value") String statusValue,
            HttpServletRequest httpServletRequest) {
        RequestIdentity requestIdentity = requestIdentityResolver.resolve(httpServletRequest);
        return ResponseEntity.ok(orderService.updateOrderStatus(id, statusValue, requestIdentity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        RequestIdentity requestIdentity = requestIdentityResolver.resolve(httpServletRequest);
        orderService.deleteOrder(id, requestIdentity);
        return ResponseEntity.noContent().build();
    }
}
