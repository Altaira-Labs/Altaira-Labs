package com.altaira.backend.repository;

import com.altaira.backend.entity.ClientEntity;
import com.altaira.backend.entity.ClientServiceEntity;
import com.altaira.backend.entity.ClientWorkspaceEntity;
import com.altaira.backend.entity.OnboardingTaskEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for onboarding task records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface OnboardingTaskRepository extends JpaRepository<OnboardingTaskEntity, UUID> {
    /**
     * Lists matching records by workspace; orders the result by sort order ascending created at ascending.
     *
     * @param workspace client workspace in scope
     * @return the onboarding task items in query order
     */
    List<OnboardingTaskEntity> findAllByWorkspaceOrderBySortOrderAscCreatedAtAsc(ClientWorkspaceEntity workspace);
    /**
     * Lists matching records by client; orders the result by sort order ascending created at ascending.
     *
     * @param client customer account in scope
     * @return the onboarding task items in query order
     */
    List<OnboardingTaskEntity> findAllByClientOrderBySortOrderAscCreatedAtAsc(ClientEntity client);

    /**
     * Lists matching records by status; orders the result by submitted at ascending.
     *
     * @param status current state in the workflow
     * @return the onboarding task items in query order
     */
    @EntityGraph(attributePaths = {"client", "clientService", "clientService.service"})
    List<OnboardingTaskEntity> findAllByStatusOrderBySubmittedAtAsc(String status);

    /**
     * Finds matching records by workspace and client service and task key.
     *
     * @param workspace client workspace in scope
     * @param clientService client operations used by this component
     * @param taskKey key identifying the task
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<OnboardingTaskEntity> findByWorkspaceAndClientServiceAndTaskKey(ClientWorkspaceEntity workspace, ClientServiceEntity clientService, String taskKey);
    /**
     * Finds matching records by workspace and client service is absent and task key.
     *
     * @param workspace client workspace in scope
     * @param taskKey key identifying the task
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<OnboardingTaskEntity> findByWorkspaceAndClientServiceIsNullAndTaskKey(ClientWorkspaceEntity workspace, String taskKey);
}
