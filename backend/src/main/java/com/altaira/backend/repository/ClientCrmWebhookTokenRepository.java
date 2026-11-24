package com.altaira.backend.repository;

import com.altaira.backend.entity.ClientCrmWebhookTokenEntity;
import com.altaira.backend.entity.ClientEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for client CRM webhook token records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface ClientCrmWebhookTokenRepository extends JpaRepository<ClientCrmWebhookTokenEntity, UUID> {
    /**
     * Finds matching records by token hash and active is true.
     *
     * @param tokenHash stored digest of the token
     * @return the matching value, or an empty optional when it is absent
     */
    @EntityGraph(attributePaths = "client")
    Optional<ClientCrmWebhookTokenEntity> findByTokenHashAndActiveTrue(String tokenHash);

    /**
     * Lists matching records by client; orders the result by created at descending.
     *
     * @param client customer account in scope
     * @return the client CRM webhook token items in query order
     */
    List<ClientCrmWebhookTokenEntity> findAllByClientOrderByCreatedAtDesc(ClientEntity client);

    /**
     * Finds matching records by id and client.
     *
     * @param id unique identifier of the record
     * @param client customer account in scope
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<ClientCrmWebhookTokenEntity> findByIdAndClient(UUID id, ClientEntity client);

    /**
     * Checks for a matching record by token hash.
     *
     * @param tokenHash stored digest of the token
     * @return true when the check succeeds
     */
    boolean existsByTokenHash(String tokenHash);
}
