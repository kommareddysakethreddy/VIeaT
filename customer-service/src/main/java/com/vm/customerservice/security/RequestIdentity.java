package com.vm.customerservice.security;

import java.util.UUID;

public record RequestIdentity(String email, String role, UUID userId) {

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
