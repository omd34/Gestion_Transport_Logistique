package com.bookingx.web;

import com.bookingx.domain.AccountStatus;
import com.bookingx.domain.EnterpriseProfile;
import com.bookingx.domain.Role;
import com.bookingx.domain.User;
import com.bookingx.repository.EnterpriseProfileRepository;
import com.bookingx.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enterprise")
@PreAuthorize("hasRole('ENTERPRISE')")
public class EnterpriseController {

    record CreateEnterpriseRequest(@NotBlank String companyName, String description, String phone) {}

    private final EnterpriseProfileRepository enterpriseProfileRepository;
    private final UserRepository userRepository;

    public EnterpriseController(EnterpriseProfileRepository enterpriseProfileRepository, UserRepository userRepository) {
        this.enterpriseProfileRepository = enterpriseProfileRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/profile")
    public ResponseEntity<?> createOrUpdateProfile(@Valid @RequestBody CreateEnterpriseRequest req, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        user.getRoles().add(Role.ENTERPRISE);
        EnterpriseProfile profile = enterpriseProfileRepository.findByUserId(user.getId()).orElseGet(EnterpriseProfile::new);
        profile.setUser(user);
        profile.setCompanyName(req.companyName());
        profile.setDescription(req.description());
        profile.setPhone(req.phone());
        if (profile.getStatus() == null) {
            profile.setStatus(AccountStatus.PENDING);
        }
        enterpriseProfileRepository.save(profile);
        userRepository.save(user);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        return enterpriseProfileRepository.findByUserId(user.getId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

