package com.altaira.backend.repository;

import com.altaira.backend.entity.ProvisioningExternalResourceEntity;
import com.altaira.backend.entity.ProvisioningPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for provisioning external resource records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface ProvisioningExternalResourceRepository extends JpaRepository<ProvisioningExternalResourceEntity, UUID> {
    /**
     * Lists matching records by plan; orders the result by provider key.
     *
     * @param plan proposed or saved delivery plan
     * @return the provisioning external resource items in query order
     */
    List<ProvisioningExternalResourceEntity> findAllByPlanOrderByProviderKey(ProvisioningPlanEntity plan);
    /**
     * Finds matching records by idempotency key.
     *
     * @param idempotencyKey key used to recognize a repeated operation
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<ProvisioningExternalResourceEntity> findByIdempotencyKey(String idempotencyKey);
    /**
     * Deletes all by plan.
     *
     * @param plan proposed or saved delivery plan
     */
    void deleteAllByPlan(ProvisioningPlanEntity plan);
}
