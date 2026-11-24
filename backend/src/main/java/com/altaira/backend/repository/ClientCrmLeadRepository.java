package com.altaira.backend.repository;

import com.altaira.backend.entity.ClientCrmLeadEntity;
import com.altaira.backend.entity.ClientEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for client CRM lead records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface ClientCrmLeadRepository extends JpaRepository<ClientCrmLeadEntity, UUID> {
    /**
     * Lists matching records by client; orders the result by created at descending.
     *
     * @param client customer account in scope
     * @return the client CRM lead items in query order
     */
    @EntityGraph(attributePaths = {"client", "createdByUser"})
    List<ClientCrmLeadEntity> findAllByClientOrderByCreatedAtDesc(ClientEntity client);

    /**
     * Finds matching records by id and client.
     *
     * @param id unique identifier of the record
     * @param client customer account in scope
     * @return the matching value, or an empty optional when it is absent
     */
    @EntityGraph(attributePaths = {"client", "createdByUser"})
    Optional<ClientCrmLeadEntity> findByIdAndClient(UUID id, ClientEntity client);
}
