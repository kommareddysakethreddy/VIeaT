package com.vm.authservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class AppUserResponseDTO {

    private final UUID id;
    private final String email;
    private final String role;
    private final Long customerId;
    private final boolean enabled;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public AppUserResponseDTO(UUID id, String email, String role, Long customerId, boolean enabled,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.customerId = customerId;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
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

    public boolean isEnabled() {
        return enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
