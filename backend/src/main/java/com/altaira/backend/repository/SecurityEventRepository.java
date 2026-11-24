package com.altaira.backend.repository;

import com.altaira.backend.entity.SecurityEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data queries for security event records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface SecurityEventRepository extends JpaRepository<SecurityEventEntity, UUID> {
    /**
     * Counts matching records by event type.
     *
     * @param eventType event type
     * @return the value produced by this operation
     */
    long countByEventType(String eventType);
}
