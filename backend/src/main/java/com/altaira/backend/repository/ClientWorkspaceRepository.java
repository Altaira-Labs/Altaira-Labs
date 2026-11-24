package com.altaira.backend.repository;

import com.altaira.backend.entity.ClientEntity;
import com.altaira.backend.entity.ClientWorkspaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for client workspace records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface ClientWorkspaceRepository extends JpaRepository<ClientWorkspaceEntity, UUID> {
    /**
     * Finds matching records by client.
     *
     * @param client customer account in scope
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<ClientWorkspaceEntity> findByClient(ClientEntity client);
}
