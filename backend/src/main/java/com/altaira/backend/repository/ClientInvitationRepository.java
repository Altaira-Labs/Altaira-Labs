package com.altaira.backend.repository;

import com.altaira.backend.entity.ClientEntity;
import com.altaira.backend.entity.ClientInvitationEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for client invitation records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface ClientInvitationRepository extends JpaRepository<ClientInvitationEntity, UUID> {
    /**
     * Lists matching records by client; orders the result by created at descending.
     *
     * @param client customer account in scope
     * @return the client invitation items in query order
     */
    List<ClientInvitationEntity> findAllByClientOrderByCreatedAtDesc(ClientEntity client);
    /**
     * Lists matching records by client and email matches without case sensitivity and accepted at is absent and revoked at is absent.
     *
     * @param client customer account in scope
     * @param email contact email address
     * @return the client invitation items
     */
    List<ClientInvitationEntity> findAllByClientAndEmailIgnoreCaseAndAcceptedAtIsNullAndRevokedAtIsNull(ClientEntity client, String email);

    /**
     * Finds matching records by token hash.
     *
     * @param tokenHash stored digest of the token
     * @return the matching value, or an empty optional when it is absent
     */
    @EntityGraph(attributePaths = "client")
    Optional<ClientInvitationEntity> findByTokenHash(String tokenHash);
}
