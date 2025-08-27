package com.bookingx.config;

import com.bookingx.domain.Role;
import com.bookingx.domain.User;
import com.bookingx.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@bookingx.local";
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setFullName("Admin User");
                admin.setPasswordHash(passwordEncoder.encode("admin123"));
                admin.getRoles().add(Role.ADMIN);
                userRepository.save(admin);
            }
        };
    }
}

