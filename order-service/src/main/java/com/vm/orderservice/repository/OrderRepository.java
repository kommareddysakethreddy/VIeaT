package com.vm.orderservice.repository;

import com.vm.orderservice.entity.Order;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);

    List<Order> findByOwnerUserIdOrderByCreatedAtDesc(UUID ownerUserId);
}
