package com.vm.notificationservice.service;

import com.vm.notificationservice.dto.NotificationResponse;
import com.vm.notificationservice.dto.OrderPlacedEvent;
import com.vm.notificationservice.entity.NotificationRecord;
import com.vm.notificationservice.exception.AccessDeniedException;
import com.vm.notificationservice.exception.ResourceNotFoundException;
import com.vm.notificationservice.repository.NotificationRecordRepository;
import com.vm.notificationservice.security.RequestIdentity;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRecordRepository notificationRecordRepository;

    public NotificationService(NotificationRecordRepository notificationRecordRepository) {
        this.notificationRecordRepository = notificationRecordRepository;
    }

    public void processOrderPlacedEvent(OrderPlacedEvent event) {
        if (notificationRecordRepository.findByOrderId(event.getOrderId()).isPresent()) {
            log.info("Notification already created for orderId={}", event.getOrderId());
            return;
        }

        NotificationRecord notificationRecord = new NotificationRecord();
        notificationRecord.setOrderId(event.getOrderId());
        notificationRecord.setCustomerId(event.getCustomerId());
        notificationRecord.setOwnerUserId(event.getOwnerUserId());
        notificationRecord.setOwnerEmail(event.getOwnerEmail());
        notificationRecord.setMessage("Your order " + event.getOrderId() + " has been placed successfully.");
        notificationRecord.setType("EMAIL");
        notificationRecord.setStatus("SENT");

        NotificationRecord savedNotificationRecord = notificationRecordRepository.save(notificationRecord);
        log.info("Saved notification record id={} for orderId={} with status={}",
                savedNotificationRecord.getId(), event.getOrderId(), savedNotificationRecord.getStatus());
    }

    public List<NotificationResponse> getAllNotifications(RequestIdentity requestIdentity) {
        List<NotificationRecord> notificationRecords = requestIdentity.isAdmin()
                ? notificationRecordRepository.findAllByOrderByCreatedAtDesc()
                : notificationRecordRepository.findAllByOwnerUserIdOrderByCreatedAtDesc(requestIdentity.userId());

        return notificationRecords
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public NotificationResponse getNotificationByOrderId(Long orderId, RequestIdentity requestIdentity) {
        NotificationRecord notificationRecord = notificationRecordRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found for order id: " + orderId));
        validateOwnership(notificationRecord, requestIdentity);
        return toResponse(notificationRecord);
    }

    private NotificationResponse toResponse(NotificationRecord notificationRecord) {
        return new NotificationResponse(
                notificationRecord.getId(),
                notificationRecord.getOrderId(),
                notificationRecord.getCustomerId(),
                notificationRecord.getMessage(),
                notificationRecord.getType(),
                notificationRecord.getStatus(),
                notificationRecord.getCreatedAt()
        );
    }

    private void validateOwnership(NotificationRecord notificationRecord, RequestIdentity requestIdentity) {
        if (!requestIdentity.isAdmin() && !requestIdentity.userId().equals(notificationRecord.getOwnerUserId())) {
            log.warn("Denied notification access for userId={} on orderId={}", requestIdentity.userId(),
                    notificationRecord.getOrderId());
            throw new AccessDeniedException("You are not allowed to access this notification");
        }
    }
}
