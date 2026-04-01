package com.vm.orderservice.security;

import java.util.UUID;

public record RequestIdentity(String email, String role, UUID userId, Long customerId) {

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
