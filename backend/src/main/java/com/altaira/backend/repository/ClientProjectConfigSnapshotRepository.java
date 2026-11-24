package com.altaira.backend.repository;

import com.altaira.backend.entity.ClientProjectConfigSnapshotEntity;
import com.altaira.backend.entity.ClientProjectEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data queries for client project config snapshot records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface ClientProjectConfigSnapshotRepository extends JpaRepository<ClientProjectConfigSnapshotEntity, UUID> {

    /**
     * Lists matching records by project; orders the result by created at descending.
     *
     * @param project project
     * @return the client project config snapshot items in query order
     */
    @EntityGraph(attributePaths = {"project", "client"})
    List<ClientProjectConfigSnapshotEntity> findAllByProjectOrderByCreatedAtDesc(ClientProjectEntity project);
}
