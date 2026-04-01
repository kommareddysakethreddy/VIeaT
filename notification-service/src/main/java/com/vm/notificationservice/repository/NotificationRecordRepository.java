package com.vm.notificationservice.repository;

import com.vm.notificationservice.entity.NotificationRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRecordRepository extends JpaRepository<NotificationRecord, Long> {

    Optional<NotificationRecord> findByOrderId(Long orderId);

    List<NotificationRecord> findAllByOrderByCreatedAtDesc();

    List<NotificationRecord> findAllByOwnerUserIdOrderByCreatedAtDesc(UUID ownerUserId);
}
