package com.altaira.backend.repository;

import com.altaira.backend.entity.ClientEntity;
import com.altaira.backend.entity.ClientProjectEntity;
import com.altaira.backend.entity.ClientServiceEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for client project records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface ClientProjectRepository extends JpaRepository<ClientProjectEntity, UUID> {
    /**
     * Finds matching records by id.
     *
     * @param id unique identifier of the record
     * @return the matching value, or an empty optional when it is absent
     */
    @Override
    @EntityGraph(attributePaths = {"client", "clientService", "clientService.service"})
    Optional<ClientProjectEntity> findById(UUID id);

    /**
     * Lists matching records by client; orders the result by created at ascending.
     *
     * @param client customer account in scope
     * @return the client project items in query order
     */
    @EntityGraph(attributePaths = {"clientService", "clientService.service"})
    List<ClientProjectEntity> findAllByClientOrderByCreatedAtAsc(ClientEntity client);

    /**
     * Lists matching records by ; orders the result by updated at descending.
     *
     * @return the client project items in query order
     */
    @EntityGraph(attributePaths = {"client", "clientService", "clientService.service"})
    List<ClientProjectEntity> findAllByOrderByUpdatedAtDesc();

    /**
     * Finds matching records by client and client service and project key.
     *
     * @param client customer account in scope
     * @param clientService client operations used by this component
     * @param projectKey key identifying the project
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<ClientProjectEntity> findByClientAndClientServiceAndProjectKey(ClientEntity client, ClientServiceEntity clientService, String projectKey);
}
