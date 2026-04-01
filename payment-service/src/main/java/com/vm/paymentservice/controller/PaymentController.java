package com.vm.paymentservice.controller;

import com.vm.paymentservice.dto.PaymentResponse;
import com.vm.paymentservice.security.RequestIdentity;
import com.vm.paymentservice.security.RequestIdentityResolver;
import com.vm.paymentservice.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final RequestIdentityResolver requestIdentityResolver;

    public PaymentController(PaymentService paymentService, RequestIdentityResolver requestIdentityResolver) {
        this.paymentService = paymentService;
        this.requestIdentityResolver = requestIdentityResolver;
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments(HttpServletRequest httpServletRequest) {
        RequestIdentity requestIdentity = requestIdentityResolver.resolve(httpServletRequest);
        return ResponseEntity.ok(paymentService.getAllPayments(requestIdentity));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable Long orderId,
                                                               HttpServletRequest httpServletRequest) {
        RequestIdentity requestIdentity = requestIdentityResolver.resolve(httpServletRequest);
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId, requestIdentity));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("service", "payment-service", "status", "UP"));
    }
}
