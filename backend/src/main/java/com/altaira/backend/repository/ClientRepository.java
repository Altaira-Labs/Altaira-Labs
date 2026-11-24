package com.altaira.backend.repository;

import com.altaira.backend.entity.ClientEntity;
import com.altaira.backend.entity.LeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for client records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {
    /**
     * Lists matching records by ; orders the result by created at descending.
     *
     * @return the client items in query order
     */
    List<ClientEntity> findAllByOrderByCreatedAtDesc();
    /**
     * Finds matching records by source lead.
     *
     * @param sourceLead enquiry from which the client was created
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<ClientEntity> findBySourceLead(LeadEntity sourceLead);
}
