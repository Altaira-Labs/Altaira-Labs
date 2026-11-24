package com.altaira.backend.repository;

import com.altaira.backend.entity.OnboardingAuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data queries for onboarding audit log records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface OnboardingAuditLogRepository extends JpaRepository<OnboardingAuditLogEntity, UUID> {
}
