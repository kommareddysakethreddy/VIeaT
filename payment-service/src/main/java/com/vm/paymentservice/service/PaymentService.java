package com.vm.paymentservice.service;

import com.vm.paymentservice.dto.OrderPlacedEvent;
import com.vm.paymentservice.dto.PaymentResponse;
import com.vm.paymentservice.entity.PaymentRecord;
import com.vm.paymentservice.exception.AccessDeniedException;
import com.vm.paymentservice.exception.ResourceNotFoundException;
import com.vm.paymentservice.repository.PaymentRecordRepository;
import com.vm.paymentservice.security.RequestIdentity;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRecordRepository paymentRecordRepository;

    public PaymentService(PaymentRecordRepository paymentRecordRepository) {
        this.paymentRecordRepository = paymentRecordRepository;
    }

    public void processOrderPlacedEvent(OrderPlacedEvent event) {
        if (paymentRecordRepository.findByOrderId(event.getOrderId()).isPresent()) {
            log.info("Payment already processed for orderId={}", event.getOrderId());
            return;
        }

        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setOrderId(event.getOrderId());
        paymentRecord.setCustomerId(event.getCustomerId());
        paymentRecord.setOwnerUserId(event.getOwnerUserId());
        paymentRecord.setOwnerEmail(event.getOwnerEmail());
        paymentRecord.setAmount(event.getAmount());
        paymentRecord.setStatus("SUCCESS");

        PaymentRecord savedPaymentRecord = paymentRecordRepository.save(paymentRecord);
        log.info("Saved payment record id={} for orderId={} with status={}",
                savedPaymentRecord.getId(), event.getOrderId(), savedPaymentRecord.getStatus());
    }

    public List<PaymentResponse> getAllPayments(RequestIdentity requestIdentity) {
        List<PaymentRecord> paymentRecords = requestIdentity.isAdmin()
                ? paymentRecordRepository.findAllByOrderByCreatedAtDesc()
                : paymentRecordRepository.findAllByOwnerUserIdOrderByCreatedAtDesc(requestIdentity.userId());

        return paymentRecords
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PaymentResponse getPaymentByOrderId(Long orderId, RequestIdentity requestIdentity) {
        PaymentRecord paymentRecord = paymentRecordRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));
        validateOwnership(paymentRecord, requestIdentity);
        return toResponse(paymentRecord);
    }

    private PaymentResponse toResponse(PaymentRecord paymentRecord) {
        return new PaymentResponse(
                paymentRecord.getId(),
                paymentRecord.getOrderId(),
                paymentRecord.getCustomerId(),
                paymentRecord.getAmount(),
                paymentRecord.getStatus(),
                paymentRecord.getCreatedAt()
        );
    }

    private void validateOwnership(PaymentRecord paymentRecord, RequestIdentity requestIdentity) {
        if (!requestIdentity.isAdmin() && !requestIdentity.userId().equals(paymentRecord.getOwnerUserId())) {
            log.warn("Denied payment access for userId={} on orderId={}", requestIdentity.userId(), paymentRecord.getOrderId());
            throw new AccessDeniedException("You are not allowed to access this payment");
        }
    }
}
