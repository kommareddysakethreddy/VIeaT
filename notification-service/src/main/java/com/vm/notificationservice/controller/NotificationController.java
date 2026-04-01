package com.vm.notificationservice.controller;

import com.vm.notificationservice.dto.NotificationResponse;
import com.vm.notificationservice.security.RequestIdentity;
import com.vm.notificationservice.security.RequestIdentityResolver;
import com.vm.notificationservice.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final RequestIdentityResolver requestIdentityResolver;

    public NotificationController(NotificationService notificationService,
                                  RequestIdentityResolver requestIdentityResolver) {
        this.notificationService = notificationService;
        this.requestIdentityResolver = requestIdentityResolver;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(HttpServletRequest httpServletRequest) {
        RequestIdentity requestIdentity = requestIdentityResolver.resolve(httpServletRequest);
        return ResponseEntity.ok(notificationService.getAllNotifications(requestIdentity));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<NotificationResponse> getNotificationByOrderId(@PathVariable Long orderId,
                                                                         HttpServletRequest httpServletRequest) {
        RequestIdentity requestIdentity = requestIdentityResolver.resolve(httpServletRequest);
        return ResponseEntity.ok(notificationService.getNotificationByOrderId(orderId, requestIdentity));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("service", "notification-service", "status", "UP"));
    }
}
