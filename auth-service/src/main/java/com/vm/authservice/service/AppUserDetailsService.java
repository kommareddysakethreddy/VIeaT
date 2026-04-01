package com.vm.authservice.service;

import com.vm.authservice.model.AppUser;
import com.vm.authservice.repository.AppUserRepository;
import java.util.Optional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public AppUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for email: " + email));

        return User.withUsername(appUser.getEmail())
                .password(appUser.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + appUser.getRole()))
                .disabled(!Optional.ofNullable(appUser.getEnabled()).orElse(false))
                .build();
    }
}
