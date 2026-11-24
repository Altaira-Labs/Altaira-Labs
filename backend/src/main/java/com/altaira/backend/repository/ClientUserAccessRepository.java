package com.altaira.backend.repository;

import com.altaira.backend.entity.AppUserEntity;
import com.altaira.backend.entity.ClientEntity;
import com.altaira.backend.entity.ClientUserAccessEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for client user access records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface ClientUserAccessRepository extends JpaRepository<ClientUserAccessEntity, UUID> {
    /**
     * Lists matching records by user and active is true.
     *
     * @param user user account in scope
     * @return the client user access items
     */
    @EntityGraph(attributePaths = "client")
    List<ClientUserAccessEntity> findAllByUserAndActiveTrue(AppUserEntity user);

    /**
     * Finds matching records by user and client and active is true.
     *
     * @param user user account in scope
     * @param client customer account in scope
     * @return the matching value, or an empty optional when it is absent
     */
    @EntityGraph(attributePaths = "client")
    Optional<ClientUserAccessEntity> findByUserAndClientAndActiveTrue(AppUserEntity user, ClientEntity client);

    /**
     * Finds matching records by user and client.
     *
     * @param user user account in scope
     * @param client customer account in scope
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<ClientUserAccessEntity> findByUserAndClient(AppUserEntity user, ClientEntity client);
}
