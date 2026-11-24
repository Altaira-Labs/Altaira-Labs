package com.altaira.backend.repository;

import com.altaira.backend.entity.ClientEntity;
import com.altaira.backend.entity.OnboardingFileEntity;
import com.altaira.backend.entity.OnboardingTaskEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for onboarding file records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface OnboardingFileRepository extends JpaRepository<OnboardingFileEntity, UUID> {
    /**
     * Lists matching records by task; orders the result by created at ascending.
     *
     * @param task task
     * @return the onboarding file items in query order
     */
    List<OnboardingFileEntity> findAllByTaskOrderByCreatedAtAsc(OnboardingTaskEntity task);
    /**
     * Lists matching records by client; orders the result by created at descending.
     *
     * @param client customer account in scope
     * @return the onboarding file items in query order
     */
    List<OnboardingFileEntity> findAllByClientOrderByCreatedAtDesc(ClientEntity client);
    /**
     * Finds matching records by storage key.
     *
     * @param storageKey key identifying the storage
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<OnboardingFileEntity> findByStorageKey(String storageKey);

    /**
     * Finds matching records by id.
     *
     * @param id unique identifier of the record
     * @return the matching value, or an empty optional when it is absent
     */
    @Override
    @EntityGraph(attributePaths = {"task", "workspace", "client"})
    Optional<OnboardingFileEntity> findById(UUID id);
}
