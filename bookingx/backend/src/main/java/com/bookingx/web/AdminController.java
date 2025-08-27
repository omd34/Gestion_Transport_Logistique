package com.bookingx.web;

import com.bookingx.domain.AccountStatus;
import com.bookingx.domain.EnterpriseProfile;
import com.bookingx.repository.EnterpriseProfileRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final EnterpriseProfileRepository enterpriseProfileRepository;

    public AdminController(EnterpriseProfileRepository enterpriseProfileRepository) {
        this.enterpriseProfileRepository = enterpriseProfileRepository;
    }

    @GetMapping("/enterprises/pending")
    public List<EnterpriseProfile> listPending() {
        return enterpriseProfileRepository.findByStatus(AccountStatus.PENDING);
    }

    @PostMapping("/enterprises/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id) {
        return enterpriseProfileRepository.findById(id)
                .map(ep -> {
                    ep.setStatus(AccountStatus.APPROVED);
                    ep.setApprovedAt(Instant.now());
                    enterpriseProfileRepository.save(ep);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/enterprises/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        return enterpriseProfileRepository.findById(id)
                .map(ep -> {
                    ep.setStatus(AccountStatus.REJECTED);
                    enterpriseProfileRepository.save(ep);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

