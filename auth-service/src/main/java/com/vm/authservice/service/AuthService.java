package com.vm.authservice.service;

import com.vm.authservice.dto.AppUserResponseDTO;
import com.vm.authservice.dto.AuthResponseDTO;
import com.vm.authservice.dto.LoginRequestDTO;
import com.vm.authservice.dto.RegisterRequestDTO;
import com.vm.authservice.model.AppUser;
import com.vm.authservice.security.JwtService;
import java.util.Locale;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_CUSTOMER = "CUSTOMER";

    private final AppUserService appUserService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomerNumberService customerNumberService;

    public AuthService(AppUserService appUserService,
                       AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       CustomerNumberService customerNumberService) {
        this.appUserService = appUserService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.customerNumberService = customerNumberService;
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (appUserService.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("A user with this email already exists");
        }

        AppUser appUser = new AppUser();
        appUser.setEmail(normalizedEmail);
        appUser.setPassword(passwordEncoder.encode(request.getPassword()));
        String resolvedRole = resolveRole(request.getRole());
        appUser.setRole(resolvedRole);
        appUser.setCustomerId(request.getCustomerId());
        if (ROLE_CUSTOMER.equals(resolvedRole)) {
            appUser.setCustomerNumber(customerNumberService.nextCustomerNumber());
        }
        appUser.setEnabled(true);

        AppUser savedUser = appUserService.save(appUser);
        return buildAuthResponse(savedUser);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().trim().toLowerCase(Locale.ROOT), request.getPassword())
        );

        AppUser appUser = appUserService.getByEmail(request.getEmail().trim().toLowerCase(Locale.ROOT));
        return buildAuthResponse(appUser);
    }

    public AppUserResponseDTO getCurrentUser(String email) {
        return appUserService.toResponse(appUserService.getByEmail(email));
    }

    private AuthResponseDTO buildAuthResponse(AppUser appUser) {
        return new AuthResponseDTO(
                jwtService.generateToken(appUser),
                appUser.getId(),
                appUser.getEmail(),
                appUser.getRole(),
                appUser.getCustomerNumber()
        );
    }

    private String resolveRole(String role) {
        if (role == null || role.isBlank()) {
            return ROLE_CUSTOMER;
        }

        String normalizedRole = role.trim().toUpperCase(Locale.ROOT);
        if (ROLE_CUSTOMER.equals(normalizedRole)) {
            return ROLE_CUSTOMER;
        }

        if (ROLE_ADMIN.equals(normalizedRole)) {
            throw new IllegalArgumentException("Public registration is available only for CUSTOMER accounts");
        }

        throw new IllegalArgumentException("Unsupported role. Allowed roles are ADMIN and CUSTOMER");
    }
}
