package com.vm.authservice.service;

import com.vm.authservice.dto.AppUserResponseDTO;
import com.vm.authservice.model.AppUser;
import com.vm.authservice.repository.AppUserRepository;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser save(AppUser appUser) {
        return appUserRepository.save(appUser);
    }

    public AppUser getByEmail(String email) {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + email));
    }

    public boolean existsByEmail(String email) {
        return appUserRepository.existsByEmail(email.toLowerCase(Locale.ROOT));
    }

    public AppUserResponseDTO toResponse(AppUser appUser) {
        return new AppUserResponseDTO(
                appUser.getId(),
                appUser.getEmail(),
                appUser.getRole(),
                appUser.getCustomerNumber(),
                Boolean.TRUE.equals(appUser.getEnabled()),
                appUser.getCreatedAt(),
                appUser.getUpdatedAt()
        );
    }
}
