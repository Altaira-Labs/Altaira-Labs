package com.altaira.backend.repository;

import com.altaira.backend.entity.ClientEntity;
import com.altaira.backend.entity.WorkspaceTaskEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for workspace task records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface WorkspaceTaskRepository extends JpaRepository<WorkspaceTaskEntity, UUID> {

    /**
     * Finds matching records by id.
     *
     * @param id unique identifier of the record
     * @return the matching value, or an empty optional when it is absent
     */
    @Override
    @EntityGraph(attributePaths = {
            "workspace",
            "client",
            "clientService",
            "clientService.service",
            "project",
            "createdByUser"
    })
    Optional<WorkspaceTaskEntity> findById(UUID id);

    /**
     * Lists matching records by ; orders the result by created at descending.
     *
     * @return the workspace task items in query order
     */
    @EntityGraph(attributePaths = {
            "workspace",
            "client",
            "clientService",
            "clientService.service",
            "project",
            "createdByUser"
    })
    List<WorkspaceTaskEntity> findAllByOrderByCreatedAtDesc();

    /**
     * Lists matching records by client; orders the result by created at descending.
     *
     * @param client customer account in scope
     * @return the workspace task items in query order
     */
    @EntityGraph(attributePaths = {
            "workspace",
            "client",
            "clientService",
            "clientService.service",
            "project",
            "createdByUser"
    })
    List<WorkspaceTaskEntity> findAllByClientOrderByCreatedAtDesc(ClientEntity client);

    /**
     * Lists matching records by client and visibility; orders the result by created at descending.
     *
     * @param client customer account in scope
     * @param visibility audience allowed to see the item
     * @return the workspace task items in query order
     */
    @EntityGraph(attributePaths = {
            "workspace",
            "client",
            "clientService",
            "clientService.service",
            "project",
            "createdByUser"
    })
    List<WorkspaceTaskEntity> findAllByClientAndVisibilityOrderByCreatedAtDesc(
            ClientEntity client,
            String visibility
    );
}
