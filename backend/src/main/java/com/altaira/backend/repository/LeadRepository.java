package com.altaira.backend.repository;

import com.altaira.backend.entity.LeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data queries for lead records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface LeadRepository extends JpaRepository<LeadEntity, UUID> {
    /**
     * Lists matching records by ; orders the result by created at descending.
     *
     * @return the lead items in query order
     */
    List<LeadEntity> findAllByOrderByCreatedAtDesc();
}
