package com.vm.orderservice.security;

import com.vm.orderservice.exception.AccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class RequestIdentityResolver {

    public RequestIdentity resolve(HttpServletRequest request) {
        String email = request.getHeader("X-Authenticated-User");
        String role = request.getHeader("X-User-Role");
        String userIdHeader = request.getHeader("X-User-Id");
        String customerIdHeader = request.getHeader("X-Customer-Id");

        if (email == null || email.isBlank() || role == null || role.isBlank() || userIdHeader == null || userIdHeader.isBlank()) {
            throw new AccessDeniedException("Missing trusted identity headers");
        }

        try {
            Long customerId = customerIdHeader == null || customerIdHeader.isBlank() ? null : Long.valueOf(customerIdHeader);
            return new RequestIdentity(email, role, UUID.fromString(userIdHeader), customerId);
        } catch (IllegalArgumentException ex) {
            throw new AccessDeniedException("Invalid trusted identity headers");
        }
    }
}
