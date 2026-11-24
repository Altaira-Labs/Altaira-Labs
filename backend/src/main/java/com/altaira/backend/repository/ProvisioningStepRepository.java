package com.altaira.backend.repository;

import com.altaira.backend.entity.ProvisioningRunEntity;
import com.altaira.backend.entity.ProvisioningStepEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data queries for provisioning step records. Method names describe the selection criteria and ordering unless an explicit query is provided.
 */
public interface ProvisioningStepRepository extends JpaRepository<ProvisioningStepEntity, UUID> {
    /**
     * Lists matching records by run; orders the result by created at.
     *
     * @param run run
     * @return the provisioning step items in query order
     */
    List<ProvisioningStepEntity> findAllByRunOrderByCreatedAt(ProvisioningRunEntity run);
    /**
     * Finds matching records by idempotency key.
     *
     * @param idempotencyKey key used to recognize a repeated operation
     * @return the matching value, or an empty optional when it is absent
     */
    Optional<ProvisioningStepEntity> findByIdempotencyKey(String idempotencyKey);
}
