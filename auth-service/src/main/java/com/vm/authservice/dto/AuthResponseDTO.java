package com.vm.authservice.dto;

import java.util.UUID;

public class AuthResponseDTO {

    private final String token;
    private final UUID userId;
    private final String email;
    private final String role;
    private final Long customerId;

    public AuthResponseDTO(String token, UUID userId, String email, String role, Long customerId) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.customerId = customerId;
    }

    public String getToken() {
        return token;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public Long getCustomerId() {
        return customerId;
    }
}
