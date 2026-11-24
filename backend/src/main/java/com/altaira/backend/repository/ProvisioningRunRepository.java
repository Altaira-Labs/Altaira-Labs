package com.altaira.backend.repository;

import com.altaira.backend.entity.ProvisioningPlanEntity;
import com.altaira.backend.entity.ProvisioningRunEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for provisioning run records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface ProvisioningRunRepository extends JpaRepository<ProvisioningRunEntity, UUID> {
    /**
     * Finds matching records by idempotency key.
     *
     * @param idempotencyKey key used to recognize a repeated operation
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<ProvisioningRunEntity> findByIdempotencyKey(String idempotencyKey);
    /**
     * Lists matching records by plan; orders the result by created at descending.
     *
     * @param plan proposed or saved delivery plan
     * @return the provisioning run items in query order
     */
    List<ProvisioningRunEntity> findAllByPlanOrderByCreatedAtDesc(ProvisioningPlanEntity plan);
}
