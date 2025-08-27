package com.bookingx.repository;

import com.bookingx.domain.AccountStatus;
import com.bookingx.domain.EnterpriseProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterpriseProfileRepository extends JpaRepository<EnterpriseProfile, Long> {
    Optional<EnterpriseProfile> findByUserId(Long userId);
    List<EnterpriseProfile> findByStatus(AccountStatus status);
}

