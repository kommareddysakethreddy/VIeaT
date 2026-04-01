package com.vm.paymentservice.repository;

import com.vm.paymentservice.entity.PaymentRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {

    Optional<PaymentRecord> findByOrderId(Long orderId);

    List<PaymentRecord> findAllByOrderByCreatedAtDesc();

    List<PaymentRecord> findAllByOwnerUserIdOrderByCreatedAtDesc(UUID ownerUserId);
}
