package com.bookingx.web;

import com.bookingx.domain.Role;
import com.bookingx.domain.User;
import com.bookingx.repository.UserRepository;
import com.bookingx.security.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    record RegisterRequest(@Email String email, @NotBlank String password, @NotBlank String fullName, String role) {}
    record LoginRequest(@Email String email, @NotBlank String password) {}
    record TokenResponse(String token) {}

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository userRepository, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already in use"));
        }
        User user = new User();
        user.setEmail(req.email());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setFullName(req.fullName());
        if (req.role() != null) {
            try {
                user.getRoles().add(Role.valueOf(req.role().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
                user.getRoles().add(Role.CLIENT);
            }
        } else {
            user.getRoles().add(Role.CLIENT);
        }
        userRepository.save(user);

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());
        String token = jwtService.generateToken(user.getEmail(), claims);
        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        User user = userRepository.findByEmail(req.email()).orElseThrow();
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());
        String token = jwtService.generateToken(user.getEmail(), claims);
        return ResponseEntity.ok(new TokenResponse(token));
    }
}

