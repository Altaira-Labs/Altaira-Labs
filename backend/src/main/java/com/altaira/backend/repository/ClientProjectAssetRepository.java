package com.altaira.backend.repository;

import com.altaira.backend.entity.ClientEntity;
import com.altaira.backend.entity.ClientProjectAssetEntity;
import com.altaira.backend.entity.ClientProjectEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for client project asset records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface ClientProjectAssetRepository extends JpaRepository<ClientProjectAssetEntity, UUID> {
    /**
     * Lists matching records by project; orders the result by uploaded at ascending.
     *
     * @param project project
     * @return the client project asset items in query order
     */
    @EntityGraph(attributePaths = {"project", "client", "uploadedByUser"})
    List<ClientProjectAssetEntity> findAllByProjectOrderByUploadedAtAsc(ClientProjectEntity project);

    /**
     * Lists matching records by client; orders the result by uploaded at descending.
     *
     * @param client customer account in scope
     * @return the client project asset items in query order
     */
    List<ClientProjectAssetEntity> findAllByClientOrderByUploadedAtDesc(ClientEntity client);
    /**
     * Finds matching records by storage key.
     *
     * @param storageKey key identifying the storage
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<ClientProjectAssetEntity> findByStorageKey(String storageKey);

    /**
     * Lists matching records by status; orders the result by uploaded at ascending.
     *
     * @param status current state in the workflow
     * @return the client project asset items in query order
     */
    @EntityGraph(attributePaths = {"project", "project.clientService", "project.clientService.service", "client"})
    List<ClientProjectAssetEntity> findAllByStatusOrderByUploadedAtAsc(String status);

    /**
     * Finds matching records by id.
     *
     * @param id unique identifier of the record
     * @return the matching value, or an empty optional when it is absent
     */
    @Override
    @EntityGraph(attributePaths = {"project", "client", "uploadedByUser"})
    Optional<ClientProjectAssetEntity> findById(UUID id);
}
